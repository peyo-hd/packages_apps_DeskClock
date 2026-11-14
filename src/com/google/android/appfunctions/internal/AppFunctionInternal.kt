package com.google.android.appfunctions.internal

import android.app.PendingIntent
import android.app.appfunctions.ExecuteAppFunctionRequest
import android.app.appfunctions.ExecuteAppFunctionResponse
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.OutcomeReceiver
import androidx.appsearch.app.GenericDocument
import appfunctions_aggregated_deps._com_android_deskclock_appaf_AppSchemaFunctions_AppFunctionInventory_Impl
import appfunctions_aggregated_deps._com_android_deskclock_appaf_AppSchemaFunctions_AppFunctionInvoker_Impl
import com.google.android.appfunctions.AppFunctionContext
import com.google.android.appfunctions.AppFunctionDataTypeMetadata
import com.google.android.appfunctions.AppFunctionDocument
import com.google.android.appfunctions.AppFunctionException
import com.google.android.appfunctions.AppFunctionMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class AppFunctionContextImpl(
    override val context: Context,
    override val callingPackageName: String,
) : AppFunctionContext

interface AppFunctionInventory {
    val functionToMetadataMap: Map<Function<*>, AppFunctionMetadata>
}

interface AppFunctionInvoker {
    val supportedFunctionIds: Set<String>

    suspend fun unsafeInvoke(
        appFunctionContext: AppFunctionContext,
        functionIdentifier: String,
        parameters: Map<String, Any?>,
    ): Any?
}

interface ClassRegistry<T> {
    fun getRegistered(): List<T>
}

class ClassRegistry_AppFunctionInventory : ClassRegistry<AppFunctionInventory> {
    override fun getRegistered(): List<AppFunctionInventory> =
        listOf(_com_android_deskclock_appaf_AppSchemaFunctions_AppFunctionInventory_Impl())
}

class ClassRegistry_AppFunctionInvoker : ClassRegistry<AppFunctionInvoker> {
    override fun getRegistered(): List<AppFunctionInvoker> =
        listOf(_com_android_deskclock_appaf_AppSchemaFunctions_AppFunctionInvoker_Impl())
}

class AggregateAppFunctionInventory(
    private val classRegistry: ClassRegistry<AppFunctionInventory>,
) : AppFunctionInventory {
    override val functionToMetadataMap: Map<Function<*>, AppFunctionMetadata> by lazy {
        val registered = classRegistry.getRegistered()
        if (registered.isEmpty()) {
            emptyMap()
        } else {
            registered.map { it.functionToMetadataMap }.reduce { acc, map -> acc + map }
        }
    }
}

class AggregateAppFunctionInvoker(
    private val classRegistry: ClassRegistry<AppFunctionInvoker>,
) : AppFunctionInvoker {
    override val supportedFunctionIds: Set<String> by lazy {
        val registered = classRegistry.getRegistered()
        if (registered.isEmpty()) {
            emptySet()
        } else {
            registered.map { it.supportedFunctionIds }.reduce { acc, set -> acc + set }
        }
    }

    override suspend fun unsafeInvoke(
        appFunctionContext: AppFunctionContext,
        functionIdentifier: String,
        parameters: Map<String, Any?>,
    ): Any? {
        val invoker = classRegistry.getRegistered()
            .firstOrNull { functionIdentifier in it.supportedFunctionIds }
            ?: throw IllegalArgumentException("Function [$functionIdentifier] is not available.")
        return invoker.unsafeInvoke(appFunctionContext, functionIdentifier, parameters)
    }
}

object KClassUtil {
    @Suppress("UNCHECKED_CAST")
    fun <T> findImpl(clazz: Class<T>, suffix: String = "_Impl"): T {
        val pkgName = clazz.`package`?.name.orEmpty()
        val canonicalName = clazz.canonicalName
            ?: throw IllegalArgumentException("Local or anonymous classes cannot be instantiated: ${clazz.name}")
        val shortName = if (pkgName.isEmpty()) canonicalName else canonicalName.substring(pkgName.length + 1)
        val implSimpleName = shortName.replace('.', '_') + suffix
        val fullName = if (pkgName.isEmpty()) implSimpleName else "$pkgName.$implSimpleName"
        val implClass = Class.forName(fullName, true, clazz.classLoader)
        return implClass.getDeclaredConstructor().newInstance() as T
    }
}

class Dependencies private constructor() {
    val appFunctionInventoryRegistry: ClassRegistry<AppFunctionInventory> by lazy {
        @Suppress("UNCHECKED_CAST")
        KClassUtil.findImpl(
            ClassRegistry::class.java as Class<ClassRegistry<AppFunctionInventory>>,
            "_AppFunctionInventory",
        )
    }

    val appFunctionInvokerRegistry: ClassRegistry<AppFunctionInvoker> by lazy {
        @Suppress("UNCHECKED_CAST")
        KClassUtil.findImpl(
            ClassRegistry::class.java as Class<ClassRegistry<AppFunctionInvoker>>,
            "_AppFunctionInvoker",
        )
    }

    val aggregateAppFunctionInventory: AggregateAppFunctionInventory by lazy {
        AggregateAppFunctionInventory(appFunctionInventoryRegistry)
    }

    val aggregateAppFunctionInvoker: AggregateAppFunctionInvoker by lazy {
        AggregateAppFunctionInvoker(appFunctionInvokerRegistry)
    }

    companion object {
        @Volatile
        private var instance: Dependencies? = null

        fun getInstance(): Dependencies =
            instance ?: synchronized(this) {
                instance ?: Dependencies().also { instance = it }
            }
    }
}

object GenericDocumentToPlatformConverter {
    fun convertToJetpackGenericDocument(
        platformDoc: android.app.appsearch.GenericDocument,
    ): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(
            platformDoc.namespace,
            platformDoc.id,
            platformDoc.schemaType,
        )
        builder.setScore(platformDoc.score)
        builder.setTtlMillis(platformDoc.ttlMillis)
        builder.setCreationTimestampMillis(platformDoc.creationTimestampMillis)

        for (propName in platformDoc.propertyNames) {
            if (propName == GenericDocument.PARENT_TYPES_SYNTHETIC_PROPERTY) {
                val parentTypes = platformDoc.getPropertyStringArray(propName)?.toList().orEmpty()
                if (parentTypes.isNotEmpty()) {
                    builder.setParentTypes(parentTypes)
                }
                continue
            }
            when (val prop = platformDoc.getProperty(propName)) {
                is Array<*> -> {
                    if (prop.isArrayOf<String>()) {
                        @Suppress("UNCHECKED_CAST")
                        builder.setPropertyString(propName, *(prop as Array<String>))
                    } else if (prop.isArrayOf<ByteArray>()) {
                        @Suppress("UNCHECKED_CAST")
                        val byteArrays = prop as Array<ByteArray>
                        if (byteArrays.isNotEmpty() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            builder.setPropertyBytes(propName, *byteArrays)
                        }
                    } else if (prop.isArrayOf<android.app.appsearch.GenericDocument>()) {
                        @Suppress("UNCHECKED_CAST")
                        val docs = prop as Array<android.app.appsearch.GenericDocument>
                        if (docs.isNotEmpty() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val converted = Array(docs.size) { convertToJetpackGenericDocument(docs[it]) }
                            builder.setPropertyDocument(propName, *converted)
                        }
                    } else if (prop.isArrayOf<android.app.appsearch.EmbeddingVector>()) {
                        throw UnsupportedOperationException("EmbeddingVector properties are not supported")
                    }
                }
                is LongArray -> builder.setPropertyLong(propName, *prop)
                is DoubleArray -> builder.setPropertyDouble(propName, *prop)
                is BooleanArray -> builder.setPropertyBoolean(propName, *prop)
            }
        }
        return builder.build()
    }

    fun convertToPlatformGenericDocument(
        jetpackDoc: GenericDocument,
    ): android.app.appsearch.GenericDocument {
        val builder = android.app.appsearch.GenericDocument.Builder<android.app.appsearch.GenericDocument.Builder<*>>(
            jetpackDoc.namespace,
            jetpackDoc.id,
            jetpackDoc.schemaType,
        )
        builder.setScore(jetpackDoc.score)
        builder.setTtlMillis(jetpackDoc.ttlMillis)
        builder.setCreationTimestampMillis(jetpackDoc.creationTimestampMillis)

        val parentTypes = jetpackDoc.parentTypes
        if (parentTypes.isNotEmpty()) {
            builder.setPropertyString(
                GenericDocument.PARENT_TYPES_SYNTHETIC_PROPERTY,
                *parentTypes.toTypedArray(),
            )
        }

        for (propName in jetpackDoc.propertyNames) {
            when (val prop = jetpackDoc.getProperty(propName)) {
                is Array<*> -> {
                    if (prop.isArrayOf<String>()) {
                        @Suppress("UNCHECKED_CAST")
                        builder.setPropertyString(propName, *(prop as Array<String>))
                    } else if (prop.isArrayOf<ByteArray>()) {
                        @Suppress("UNCHECKED_CAST")
                        builder.setPropertyBytes(propName, *(prop as Array<ByteArray>))
                    } else if (prop.isArrayOf<GenericDocument>()) {
                        @Suppress("UNCHECKED_CAST")
                        val docs = prop as Array<GenericDocument>
                        val converted = Array(docs.size) { convertToPlatformGenericDocument(docs[it]) }
                        builder.setPropertyDocument(propName, *converted)
                    } else if (prop.isArrayOf<androidx.appsearch.app.EmbeddingVector>()) {
                        throw UnsupportedOperationException("EmbeddingVector properties are not supported")
                    }
                }
                is LongArray -> builder.setPropertyLong(propName, *prop)
                is DoubleArray -> builder.setPropertyDouble(propName, *prop)
                is BooleanArray -> builder.setPropertyBoolean(propName, *prop)
            }
        }
        return builder.build()
    }
}

internal class AppFunctionServiceDelegate(
    private val appContext: Context,
    private val workerCoroutineContext: CoroutineContext,
    private val mainCoroutineContext: CoroutineContext,
    private val aggregateAppFunctionInventory: AggregateAppFunctionInventory =
        Dependencies.getInstance().aggregateAppFunctionInventory,
    private val aggregateAppFunctionInvoker: AggregateAppFunctionInvoker =
        Dependencies.getInstance().aggregateAppFunctionInvoker,
) {
    fun onExecuteFunction(
        request: ExecuteAppFunctionRequest,
        callingPackageName: String,
        scope: CoroutineScope,
        callback: OutcomeReceiver<ExecuteAppFunctionResponse, android.app.appfunctions.AppFunctionException>,
    ): Job = scope.launch(mainCoroutineContext) {
        try {
            val response = invokeFunctionInternal(request, callingPackageName)
            callback.onResult(response)
        } catch (e: CancellationException) {
            callback.onError(
                android.app.appfunctions.AppFunctionException(
                    AppFunctionException.ERROR_CANCELLED,
                    e.message,
                    Bundle.EMPTY,
                )
            )
        } catch (e: AppFunctionException) {
            callback.onError(
                android.app.appfunctions.AppFunctionException(
                    e.errorCode,
                    e.errorMessage,
                    e.extras,
                )
            )
        } catch (e: IllegalArgumentException) {
            callback.onError(
                android.app.appfunctions.AppFunctionException(
                    AppFunctionException.ERROR_INVALID_ARGUMENT,
                    e.message,
                    Bundle.EMPTY,
                )
            )
        } catch (t: Throwable) {
            callback.onError(
                android.app.appfunctions.AppFunctionException(
                    AppFunctionException.ERROR_APP_UNKNOWN_ERROR,
                    t.message,
                    Bundle.EMPTY,
                )
            )
        }
    }

    private suspend fun invokeFunctionInternal(
        request: ExecuteAppFunctionRequest,
        callingPackageName: String,
    ): ExecuteAppFunctionResponse {
        val metadata = aggregateAppFunctionInventory.functionToMetadataMap.values
            .firstOrNull { it.id == request.functionIdentifier }
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_FUNCTION_NOT_FOUND,
                "Function [${request.functionIdentifier}] is not found",
            )

        val requestDoc = AppFunctionDocument(
            GenericDocumentToPlatformConverter.convertToJetpackGenericDocument(request.parameters),
            request.extras,
        )
        val paramsMap = extractParameters(metadata, requestDoc)
        val result = withContext(workerCoroutineContext) {
            aggregateAppFunctionInvoker.unsafeInvoke(
                AppFunctionContextImpl(appContext, callingPackageName),
                request.functionIdentifier,
                paramsMap,
            )
        }
        val responseDoc = buildResponseDocument(metadata.returnType, result)
        val platformDoc = GenericDocumentToPlatformConverter.convertToPlatformGenericDocument(
            responseDoc.genericDocument
        )
        return ExecuteAppFunctionResponse(platformDoc, responseDoc.extras)
    }

    private fun extractParameters(
        metadata: AppFunctionMetadata,
        document: AppFunctionDocument,
    ): Map<String, Any?> {
        val result = LinkedHashMap<String, Any?>(metadata.parameters.size)
        for (param in metadata.parameters) {
            val type = param.parameterType
            val name = param.name
            val value: Any? = if (type.isList) {
                when (type.dataType) {
                    AppFunctionDataTypeMetadata.DATA_TYPE_BOOLEAN -> document.getBooleanArray(name)?.toList()
                    AppFunctionDataTypeMetadata.DATA_TYPE_BYTES -> document.getBytesArray(name)?.toList()
                    AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT -> {
                        val docClass = Class.forName(requireNotNull(type.documentType))
                        document.getDocumentArray(name)?.map { it.genericDocument.toDocumentClass(docClass) }
                    }
                    AppFunctionDataTypeMetadata.DATA_TYPE_DOUBLE -> document.getDoubleArray(name)?.toList()
                    AppFunctionDataTypeMetadata.DATA_TYPE_FLOAT -> document.getFloatArray(name)?.toList()
                    AppFunctionDataTypeMetadata.DATA_TYPE_LONG -> document.getLongArray(name)?.toList()
                    AppFunctionDataTypeMetadata.DATA_TYPE_INT -> document.getIntArray(name)?.toList()
                    AppFunctionDataTypeMetadata.DATA_TYPE_STRING -> document.getStringArray(name)?.toList()
                    else -> null
                }
            } else {
                when (type.dataType) {
                    AppFunctionDataTypeMetadata.DATA_TYPE_BOOLEAN -> document.getBoolean(name)
                    AppFunctionDataTypeMetadata.DATA_TYPE_BYTES -> document.getBytes(name)
                    AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT -> {
                        val docClass = Class.forName(requireNotNull(type.documentType))
                        document.getDocument(name)?.genericDocument?.toDocumentClass(docClass)
                    }
                    AppFunctionDataTypeMetadata.DATA_TYPE_DOUBLE -> document.getDouble(name)
                    AppFunctionDataTypeMetadata.DATA_TYPE_FLOAT -> document.getFloat(name)
                    AppFunctionDataTypeMetadata.DATA_TYPE_LONG -> document.getLong(name)
                    AppFunctionDataTypeMetadata.DATA_TYPE_INT -> document.getInt(name)
                    AppFunctionDataTypeMetadata.DATA_TYPE_STRING -> document.getString(name)
                    AppFunctionDataTypeMetadata.DATA_TYPE_PENDING_INTENT -> document.getPendingIntent(name)
                    else -> null
                }
            }
            if (value == null && !type.isNullable) {
                throw IllegalArgumentException("Missing required parameter [$name]")
            }
            result[name] = value
        }
        return result
    }

    private fun buildResponseDocument(
        returnType: AppFunctionDataTypeMetadata,
        result: Any?,
    ): AppFunctionDocument {
        if (result == null || result is Unit || returnType.dataType == AppFunctionDataTypeMetadata.DATA_TYPE_UNIT) {
            return AppFunctionDocument.EMPTY
        }
        val key = ExecuteAppFunctionResponse.PROPERTY_RETURN_VALUE
        val builder = AppFunctionDocument.Builder("", "", "")
        if (returnType.isList) {
            val list = result as List<*>
            when (returnType.dataType) {
                AppFunctionDataTypeMetadata.DATA_TYPE_BOOLEAN ->
                    builder.setBooleanArray(key, BooleanArray(list.size) { list[it] as Boolean })
                AppFunctionDataTypeMetadata.DATA_TYPE_BYTES ->
                    builder.setBytesArray(key, Array(list.size) { list[it] as ByteArray })
                AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT ->
                    builder.setDocumentArray(
                        key,
                        Array(list.size) { item ->
                            val value = list[item]
                            if (value is AppFunctionDocument) value
                            else AppFunctionDocument.fromDocumentClass(requireNotNull(value))
                        },
                    )
                AppFunctionDataTypeMetadata.DATA_TYPE_DOUBLE ->
                    builder.setDoubleArray(key, DoubleArray(list.size) { list[it] as Double })
                AppFunctionDataTypeMetadata.DATA_TYPE_FLOAT ->
                    builder.setFloatArray(key, FloatArray(list.size) { list[it] as Float })
                AppFunctionDataTypeMetadata.DATA_TYPE_LONG ->
                    builder.setLongArray(key, LongArray(list.size) { list[it] as Long })
                AppFunctionDataTypeMetadata.DATA_TYPE_INT ->
                    builder.setIntArray(key, IntArray(list.size) { list[it] as Int })
                AppFunctionDataTypeMetadata.DATA_TYPE_STRING ->
                    builder.setStringArray(key, Array(list.size) { list[it] as String })
            }
        } else {
            when (returnType.dataType) {
                AppFunctionDataTypeMetadata.DATA_TYPE_BOOLEAN -> builder.setBoolean(key, result as Boolean)
                AppFunctionDataTypeMetadata.DATA_TYPE_BYTES -> builder.setBytes(key, result as ByteArray)
                AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT -> {
                    val doc = if (result is AppFunctionDocument) result else AppFunctionDocument.fromDocumentClass(result)
                    builder.setDocument(key, doc)
                }
                AppFunctionDataTypeMetadata.DATA_TYPE_DOUBLE -> builder.setDouble(key, result as Double)
                AppFunctionDataTypeMetadata.DATA_TYPE_FLOAT -> builder.setFloat(key, result as Float)
                AppFunctionDataTypeMetadata.DATA_TYPE_LONG -> builder.setLong(key, result as Long)
                AppFunctionDataTypeMetadata.DATA_TYPE_INT -> builder.setInt(key, result as Int)
                AppFunctionDataTypeMetadata.DATA_TYPE_STRING -> builder.setString(key, result as String)
                AppFunctionDataTypeMetadata.DATA_TYPE_PENDING_INTENT ->
                    builder.setPendingIntent(key, result as PendingIntent)
            }
        }
        return builder.build()
    }
}

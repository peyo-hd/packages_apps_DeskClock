package com.google.android.appfunctions

import android.app.PendingIntent
import android.app.appfunctions.AppFunctionManager
import android.app.appfunctions.ExecuteAppFunctionResponse
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.OutcomeReceiver
import android.os.Parcelable
import android.widget.RemoteViews
import androidx.appsearch.app.AppSearchSchema
import androidx.appsearch.app.DocumentClassFactory
import androidx.appsearch.app.DocumentClassMappingContext
import androidx.appsearch.app.GenericDocument
import com.google.android.appfunctions.internal.Dependencies
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class AppFunction(
    val description: String,
    val enabledByDefault: Boolean = true,
    val restrictCallersWithExecuteAppFunctions: Boolean = false,
    val displayNameStringRes: Int = 0,
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class AppParam(
    val description: String,
)

interface AppFunctionContext {
    val context: Context
    val callingPackageName: String
}

enum class EnabledState {
    APP_FUNCTION_STATE_DEFAULT,
    APP_FUNCTION_STATE_ENABLED,
    APP_FUNCTION_STATE_DISABLED,
}

class AppFunctionException(
    val errorCode: Int,
    val errorMessage: String? = null,
    val extras: Bundle = Bundle.EMPTY,
) : Exception(errorMessage) {

    val errorCategory: Int
        get() = when (errorCode) {
            in 1000..1999 -> ERROR_CATEGORY_REQUEST_ERROR
            in 2000..2999 -> ERROR_CATEGORY_SYSTEM
            in 3000..3999 -> ERROR_CATEGORY_APP
            else -> ERROR_CATEGORY_UNKNOWN
        }

    companion object {
        const val ERROR_CATEGORY_UNKNOWN = 0
        const val ERROR_CATEGORY_REQUEST_ERROR = 1
        const val ERROR_CATEGORY_SYSTEM = 2
        const val ERROR_CATEGORY_APP = 3

        const val ERROR_DENIED = 1000
        const val ERROR_INVALID_ARGUMENT = 1001
        const val ERROR_DISABLED = 1002
        const val ERROR_FUNCTION_NOT_FOUND = 1003
        const val ERROR_NOT_FOUND = 1500
        const val ERROR_LIMIT_EXCEEDED = 1501
        const val ERROR_ALREADY_EXISTS = 1502

        const val ERROR_SYSTEM_ERROR = 2000
        const val ERROR_CANCELLED = 2001

        const val ERROR_APP_UNKNOWN_ERROR = 3000
        const val ERROR_PERMISSION_REQUIRED = 3500
        const val ERROR_ACTION_NOT_SUPPORTED = 3501
    }
}

class AppFunctionDataTypeMetadata(
    val id: String,
    val namespace: String,
    val dataType: Int,
    val isList: Boolean,
    val isNullable: Boolean,
    val documentType: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppFunctionDataTypeMetadata) return false
        return id == other.id &&
            namespace == other.namespace &&
            dataType == other.dataType &&
            isList == other.isList &&
            isNullable == other.isNullable &&
            documentType == other.documentType
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + namespace.hashCode()
        result = 31 * result + dataType
        result = 31 * result + isList.hashCode()
        result = 31 * result + isNullable.hashCode()
        result = 31 * result + (documentType?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "AppFunctionDataTypeMetadata(id='$id', namespace='$namespace', dataType=$dataType, " +
            "isList=$isList, isNullable=$isNullable, documentType=$documentType)"

    companion object {
        const val DATA_TYPE_UNIT = 0
        const val DATA_TYPE_BOOLEAN = 1
        const val DATA_TYPE_BYTES = 2
        const val DATA_TYPE_OBJECT = 3
        const val DATA_TYPE_DOUBLE = 4
        const val DATA_TYPE_FLOAT = 5
        const val DATA_TYPE_LONG = 6
        const val DATA_TYPE_INT = 7
        const val DATA_TYPE_STRING = 8
        const val DATA_TYPE_PENDING_INTENT = 9
    }
}

class `$$__AppSearch__AppFunctionDataTypeMetadata` :
    DocumentClassFactory<AppFunctionDataTypeMetadata> {

    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(
                AppSearchSchema.LongPropertyConfig.Builder("dataType")
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .setIndexingType(AppSearchSchema.LongPropertyConfig.INDEXING_TYPE_NONE)
                    .build()
            )
            .addProperty(
                AppSearchSchema.BooleanPropertyConfig.Builder("isList")
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .build()
            )
            .addProperty(
                AppSearchSchema.BooleanPropertyConfig.Builder("isNullable")
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .build()
            )
            .addProperty(
                AppSearchSchema.StringPropertyConfig.Builder("documentType")
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .setTokenizerType(AppSearchSchema.StringPropertyConfig.TOKENIZER_NONE)
                    .setIndexingType(AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE)
                    .setJoinableValueType(AppSearchSchema.StringPropertyConfig.JOINABLE_VALUE_TYPE_NONE)
                    .build()
            )
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()

    override fun toGenericDocument(document: AppFunctionDataTypeMetadata): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(
            document.namespace,
            document.id,
            SCHEMA_NAME,
        )
        builder.setPropertyLong("dataType", document.dataType.toLong())
        builder.setPropertyBoolean("isList", document.isList)
        builder.setPropertyBoolean("isNullable", document.isNullable)
        document.documentType?.let { builder.setPropertyString("documentType", it) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): AppFunctionDataTypeMetadata = AppFunctionDataTypeMetadata(
        id = genericDoc.id,
        namespace = genericDoc.namespace,
        dataType = genericDoc.getPropertyLong("dataType").toInt(),
        isList = genericDoc.getPropertyBoolean("isList"),
        isNullable = genericDoc.getPropertyBoolean("isNullable"),
        documentType = genericDoc.getPropertyString("documentType"),
    )

    companion object {
        const val SCHEMA_NAME = "AppFunctionDataTypeMetadata"
    }
}

class AppFunctionParameterMetadata(
    val id: String,
    val namespace: String,
    val name: String,
    val description: String,
    val parameterType: AppFunctionDataTypeMetadata,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppFunctionParameterMetadata) return false
        return id == other.id &&
            namespace == other.namespace &&
            name == other.name &&
            description == other.description &&
            parameterType == other.parameterType
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + namespace.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + parameterType.hashCode()
        return result
    }

    override fun toString(): String =
        "AppFunctionParameterMetadata(id='$id', namespace='$namespace', name='$name', " +
            "description='$description', parameterType=$parameterType)"
}

class `$$__AppSearch__AppFunctionParameterMetadata` :
    DocumentClassFactory<AppFunctionParameterMetadata> {

    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(
                AppSearchSchema.StringPropertyConfig.Builder("name")
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .setTokenizerType(AppSearchSchema.StringPropertyConfig.TOKENIZER_NONE)
                    .setIndexingType(AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE)
                    .setJoinableValueType(AppSearchSchema.StringPropertyConfig.JOINABLE_VALUE_TYPE_NONE)
                    .build()
            )
            .addProperty(
                AppSearchSchema.StringPropertyConfig.Builder("description")
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .setTokenizerType(AppSearchSchema.StringPropertyConfig.TOKENIZER_NONE)
                    .setIndexingType(AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE)
                    .setJoinableValueType(AppSearchSchema.StringPropertyConfig.JOINABLE_VALUE_TYPE_NONE)
                    .build()
            )
            .addProperty(
                AppSearchSchema.DocumentPropertyConfig.Builder(
                    "parameterType",
                    `$$__AppSearch__AppFunctionDataTypeMetadata`.SCHEMA_NAME,
                )
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .setShouldIndexNestedProperties(false)
                    .build()
            )
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> =
        listOf(AppFunctionDataTypeMetadata::class.java)

    override fun toGenericDocument(document: AppFunctionParameterMetadata): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(
            document.namespace,
            document.id,
            SCHEMA_NAME,
        )
        builder.setPropertyString("name", document.name)
        builder.setPropertyString("description", document.description)
        builder.setPropertyDocument(
            "parameterType",
            GenericDocument.fromDocumentClass(document.parameterType),
        )
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): AppFunctionParameterMetadata {
        val typeDoc = genericDoc.getPropertyDocument("parameterType")
            ?: throw IllegalStateException("Missing parameterType property")
        return AppFunctionParameterMetadata(
            id = genericDoc.id,
            namespace = genericDoc.namespace,
            name = genericDoc.getPropertyString("name") ?: "",
            description = genericDoc.getPropertyString("description") ?: "",
            parameterType = typeDoc.toDocumentClass(
                AppFunctionDataTypeMetadata::class.java,
                mappingContext,
            ),
        )
    }

    companion object {
        const val SCHEMA_NAME = "AppFunctionParameterMetadata"
    }
}

class AppFunctionMetadata(
    val id: String,
    val namespace: String,
    val enabledByDefault: Boolean,
    val description: String,
    val parameters: List<AppFunctionParameterMetadata>,
    val returnType: AppFunctionDataTypeMetadata,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppFunctionMetadata) return false
        return id == other.id &&
            namespace == other.namespace &&
            enabledByDefault == other.enabledByDefault &&
            description == other.description &&
            parameters == other.parameters &&
            returnType == other.returnType
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + namespace.hashCode()
        result = 31 * result + enabledByDefault.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + parameters.hashCode()
        result = 31 * result + returnType.hashCode()
        return result
    }

    override fun toString(): String =
        "AppFunctionMetadata(id='$id', namespace='$namespace', enabledByDefault=$enabledByDefault, " +
            "description='$description', parameters=$parameters, returnType=$returnType)"
}

class `$$__AppSearch__AppFunctionMetadata` :
    DocumentClassFactory<AppFunctionMetadata> {

    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(
                AppSearchSchema.BooleanPropertyConfig.Builder("enabledByDefault")
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .build()
            )
            .addProperty(
                AppSearchSchema.StringPropertyConfig.Builder("description")
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .setTokenizerType(AppSearchSchema.StringPropertyConfig.TOKENIZER_NONE)
                    .setIndexingType(AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE)
                    .setJoinableValueType(AppSearchSchema.StringPropertyConfig.JOINABLE_VALUE_TYPE_NONE)
                    .build()
            )
            .addProperty(
                AppSearchSchema.DocumentPropertyConfig.Builder(
                    "parameters",
                    `$$__AppSearch__AppFunctionParameterMetadata`.SCHEMA_NAME,
                )
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)
                    .setShouldIndexNestedProperties(false)
                    .build()
            )
            .addProperty(
                AppSearchSchema.DocumentPropertyConfig.Builder(
                    "returnType",
                    `$$__AppSearch__AppFunctionDataTypeMetadata`.SCHEMA_NAME,
                )
                    .setCardinality(AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)
                    .setShouldIndexNestedProperties(false)
                    .build()
            )
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(
        AppFunctionParameterMetadata::class.java,
        AppFunctionDataTypeMetadata::class.java,
    )

    override fun toGenericDocument(document: AppFunctionMetadata): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(
            document.namespace,
            document.id,
            SCHEMA_NAME,
        )
        builder.setPropertyBoolean("enabledByDefault", document.enabledByDefault)
        builder.setPropertyString("description", document.description)
        if (document.parameters.isNotEmpty()) {
            builder.setPropertyDocument(
                "parameters",
                *document.parameters.map { GenericDocument.fromDocumentClass(it) }.toTypedArray(),
            )
        }
        builder.setPropertyDocument(
            "returnType",
            GenericDocument.fromDocumentClass(document.returnType),
        )
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): AppFunctionMetadata {
        val paramDocs = genericDoc.getPropertyDocumentArray("parameters")
        val params = paramDocs?.map {
            it.toDocumentClass(AppFunctionParameterMetadata::class.java, mappingContext)
        } ?: emptyList()
        val returnTypeDoc = genericDoc.getPropertyDocument("returnType")
            ?: throw IllegalStateException("Missing returnType property")
        return AppFunctionMetadata(
            id = genericDoc.id,
            namespace = genericDoc.namespace,
            enabledByDefault = genericDoc.getPropertyBoolean("enabledByDefault"),
            description = genericDoc.getPropertyString("description") ?: "",
            parameters = params,
            returnType = returnTypeDoc.toDocumentClass(
                AppFunctionDataTypeMetadata::class.java,
                mappingContext,
            ),
        )
    }

    companion object {
        const val SCHEMA_NAME = "AppFunctionMetadata"
    }
}

class AppFunctionDocument(
    val genericDocument: GenericDocument,
    val extras: Bundle = Bundle.EMPTY,
) {
    fun getBoolean(key: String): Boolean? {
        val arr = getBooleanArray(key) ?: return null
        require(arr.size <= 1) { "Property [$key] contains more than one element" }
        return arr.firstOrNull()
    }

    fun getBooleanArray(key: String): BooleanArray? = genericDocument.getPropertyBooleanArray(key)

    fun getBytes(key: String): ByteArray? {
        val arr = getBytesArray(key) ?: return null
        require(arr.size <= 1) { "Property [$key] contains more than one element" }
        return arr.firstOrNull()
    }

    fun getBytesArray(key: String): Array<ByteArray>? = genericDocument.getPropertyBytesArray(key)

    fun getDocument(key: String): AppFunctionDocument? {
        val arr = getDocumentArray(key) ?: return null
        require(arr.size <= 1) { "Property [$key] contains more than one element" }
        return arr.firstOrNull()
    }

    fun getDocumentArray(key: String): Array<AppFunctionDocument>? {
        val docs = genericDocument.getPropertyDocumentArray(key) ?: return null
        return Array(docs.size) { idx ->
            val nestedExtras = extras.getBundle(extrasKey(key, idx))
                ?: extras.getBundle(extrasKey(key))
                ?: Bundle.EMPTY
            AppFunctionDocument(docs[idx], nestedExtras)
        }
    }

    fun getDouble(key: String): Double? {
        val arr = getDoubleArray(key) ?: return null
        require(arr.size <= 1) { "Property [$key] contains more than one element" }
        return arr.firstOrNull()
    }

    fun getDoubleArray(key: String): DoubleArray? = genericDocument.getPropertyDoubleArray(key)

    fun getFloat(key: String): Float? {
        val arr = getFloatArray(key) ?: return null
        require(arr.size <= 1) { "Property [$key] contains more than one element" }
        return arr.firstOrNull()
    }

    fun getFloatArray(key: String): FloatArray? {
        val doubles = genericDocument.getPropertyDoubleArray(key) ?: return null
        return FloatArray(doubles.size) { doubles[it].toFloat() }
    }

    fun getInt(key: String): Int? {
        val arr = getIntArray(key) ?: return null
        require(arr.size <= 1) { "Property [$key] contains more than one element" }
        return arr.firstOrNull()
    }

    fun getIntArray(key: String): IntArray? {
        val longs = genericDocument.getPropertyLongArray(key) ?: return null
        return IntArray(longs.size) { longs[it].toInt() }
    }

    fun getLong(key: String): Long? {
        val arr = getLongArray(key) ?: return null
        require(arr.size <= 1) { "Property [$key] contains more than one element" }
        return arr.firstOrNull()
    }

    fun getLongArray(key: String): LongArray? = genericDocument.getPropertyLongArray(key)

    fun getString(key: String): String? {
        val arr = getStringArray(key) ?: return null
        require(arr.size <= 1) { "Property [$key] contains more than one element" }
        return arr.firstOrNull()
    }

    fun getStringArray(key: String): Array<String>? = genericDocument.getPropertyStringArray(key)

    fun getPendingIntent(key: String): PendingIntent? = getParcelableCompat(extrasKey(key))

    fun getBitmap(key: String): Bitmap? = getParcelableCompat(extrasKey(key))

    fun getRemoteViews(key: String): RemoteViews? = getParcelableCompat(extrasKey(key))

    private inline fun <reified T : Parcelable> getParcelableCompat(key: String): T? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            extras.getParcelable(key)
        }

    class Builder {
        private val docBuilder: GenericDocument.Builder<*>
        private val extrasBuilder: Bundle

        constructor(namespace: String, id: String, schemaType: String) {
            docBuilder = GenericDocument.Builder<GenericDocument.Builder<*>>(namespace, id, schemaType)
            extrasBuilder = Bundle()
        }

        constructor(document: AppFunctionDocument) {
            docBuilder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.genericDocument)
            extrasBuilder = Bundle(document.extras)
        }

        fun setNamespace(namespace: String): Builder = apply { docBuilder.setNamespace(namespace) }

        fun setId(id: String): Builder = apply { docBuilder.setId(id) }

        fun setSchemaType(schemaType: String): Builder = apply { docBuilder.setSchemaType(schemaType) }

        fun setBoolean(key: String, value: Boolean): Builder = apply {
            docBuilder.setPropertyBoolean(key, value)
        }

        fun setBooleanArray(key: String, values: BooleanArray): Builder = apply {
            docBuilder.setPropertyBoolean(key, *values)
        }

        fun setBytes(key: String, value: ByteArray): Builder = apply {
            docBuilder.setPropertyBytes(key, value)
        }

        fun setBytesArray(key: String, values: Array<ByteArray>): Builder = apply {
            docBuilder.setPropertyBytes(key, *values)
        }

        fun setDocument(key: String, value: AppFunctionDocument): Builder = apply {
            docBuilder.setPropertyDocument(key, value.genericDocument)
            if (!value.extras.isEmpty) {
                extrasBuilder.putBundle(extrasKey(key), Bundle(value.extras))
            }
        }

        fun setDocumentArray(key: String, values: Array<AppFunctionDocument>): Builder = apply {
            docBuilder.setPropertyDocument(key, *values.map { it.genericDocument }.toTypedArray())
            values.forEachIndexed { idx, doc ->
                if (!doc.extras.isEmpty) {
                    extrasBuilder.putBundle(extrasKey(key, idx), Bundle(doc.extras))
                }
            }
        }

        fun setDouble(key: String, value: Double): Builder = apply {
            docBuilder.setPropertyDouble(key, value)
        }

        fun setDoubleArray(key: String, values: DoubleArray): Builder = apply {
            docBuilder.setPropertyDouble(key, *values)
        }

        fun setFloat(key: String, value: Float): Builder = apply {
            docBuilder.setPropertyDouble(key, value.toDouble())
        }

        fun setFloatArray(key: String, values: FloatArray): Builder = apply {
            docBuilder.setPropertyDouble(key, *DoubleArray(values.size) { values[it].toDouble() })
        }

        fun setInt(key: String, value: Int): Builder = apply {
            docBuilder.setPropertyLong(key, value.toLong())
        }

        fun setIntArray(key: String, values: IntArray): Builder = apply {
            docBuilder.setPropertyLong(key, *LongArray(values.size) { values[it].toLong() })
        }

        fun setLong(key: String, value: Long): Builder = apply {
            docBuilder.setPropertyLong(key, value)
        }

        fun setLongArray(key: String, values: LongArray): Builder = apply {
            docBuilder.setPropertyLong(key, *values)
        }

        fun setString(key: String, value: String): Builder = apply {
            docBuilder.setPropertyString(key, value)
        }

        fun setStringArray(key: String, values: Array<String>): Builder = apply {
            docBuilder.setPropertyString(key, *values)
        }

        fun setBitmap(key: String, value: Bitmap): Builder = apply {
            require(key == ExecuteAppFunctionResponse.PROPERTY_RETURN_VALUE) {
                "Bitmap property only supported for ${ExecuteAppFunctionResponse.PROPERTY_RETURN_VALUE}"
            }
            extrasBuilder.putParcelable(extrasKey(key), value)
        }

        fun setPendingIntent(key: String, value: PendingIntent): Builder = apply {
            require(key == ExecuteAppFunctionResponse.PROPERTY_RETURN_VALUE) {
                "PendingIntent property only supported for ${ExecuteAppFunctionResponse.PROPERTY_RETURN_VALUE}"
            }
            extrasBuilder.putParcelable(extrasKey(key), value)
        }

        fun setRemoteViews(key: String, value: RemoteViews): Builder = apply {
            require(key == ExecuteAppFunctionResponse.PROPERTY_RETURN_VALUE) {
                "RemoteViews property only supported for ${ExecuteAppFunctionResponse.PROPERTY_RETURN_VALUE}"
            }
            extrasBuilder.putParcelable(extrasKey(key), value)
        }

        fun build(): AppFunctionDocument =
            AppFunctionDocument(
                genericDocument = docBuilder.build(),
                extras = if (extrasBuilder.isEmpty) Bundle.EMPTY else Bundle(extrasBuilder),
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppFunctionDocument) return false
        return genericDocument == other.genericDocument
    }

    override fun hashCode(): Int = genericDocument.hashCode()

    override fun toString(): String = "AppFunctionDocument(genericDocument=$genericDocument)"

    companion object {
        val EMPTY: AppFunctionDocument = AppFunctionDocument(GenericDocument.EMPTY, Bundle.EMPTY)

        fun fromDocumentClass(document: Any): AppFunctionDocument =
            AppFunctionDocument(GenericDocument.fromDocumentClass(document), Bundle.EMPTY)

        fun extrasKey(key: String): String = "property/$key"

        fun extrasKey(key: String, index: Int): String = "property/$key[$index]"
    }
}

object AppFunctions {
    suspend fun isAppFunctionEnabled(context: Context, function: Function<*>): Boolean {
        val functionId = Dependencies.getInstance()
            .aggregateAppFunctionInventory
            .functionToMetadataMap[function]
            ?.id
            ?: throw IllegalArgumentException("Function [$function] is not registered")
        val manager = context.getSystemService(AppFunctionManager::class.java)
            ?: throw IllegalStateException("AppFunctionManager system service is not available")
        return suspendCancellableCoroutine { continuation ->
            manager.isAppFunctionEnabled(
                functionId,
                context.mainExecutor,
                object : OutcomeReceiver<Boolean, Exception> {
                    override fun onResult(result: Boolean) {
                        continuation.resume(result)
                    }

                    override fun onError(error: Exception) {
                        continuation.resumeWithException(error)
                    }
                },
            )
        }
    }

    suspend fun setAppFunctionEnabled(
        context: Context,
        function: Function<*>,
        newEnabledState: EnabledState,
    ) {
        val functionId = Dependencies.getInstance()
            .aggregateAppFunctionInventory
            .functionToMetadataMap[function]
            ?.id
            ?: throw IllegalArgumentException("Function [$function] is not registered")
        val manager = context.getSystemService(AppFunctionManager::class.java)
            ?: throw IllegalStateException("AppFunctionManager system service is not available")
        val platformState = toPlatformEnabledState(newEnabledState)
        suspendCancellableCoroutine<Unit> { continuation ->
            manager.setAppFunctionEnabled(
                functionId,
                platformState,
                context.mainExecutor,
                object : OutcomeReceiver<Void, Exception> {
                    override fun onResult(result: Void?) {
                        continuation.resume(Unit)
                    }

                    override fun onError(error: Exception) {
                        continuation.resumeWithException(error)
                    }
                },
            )
        }
    }

    private fun toPlatformEnabledState(state: EnabledState): Int = when (state) {
        EnabledState.APP_FUNCTION_STATE_DEFAULT -> AppFunctionManager.APP_FUNCTION_STATE_DEFAULT
        EnabledState.APP_FUNCTION_STATE_ENABLED -> AppFunctionManager.APP_FUNCTION_STATE_ENABLED
        EnabledState.APP_FUNCTION_STATE_DISABLED -> AppFunctionManager.APP_FUNCTION_STATE_DISABLED
    }
}

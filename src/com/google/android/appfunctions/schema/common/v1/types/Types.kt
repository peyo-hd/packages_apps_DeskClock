package com.google.android.appfunctions.schema.common.v1.types

import androidx.appsearch.app.AppSearchSchema
import androidx.appsearch.app.DocumentClassFactory
import androidx.appsearch.app.DocumentClassMappingContext
import androidx.appsearch.app.GenericDocument

interface SetField<out T> {
    val value: T
}

interface SetFieldCompanion<T> {
    fun setField(value: T): SetField<T>
    fun setListField(value: List<T>): SetField<List<T>>
    fun setNullableField(value: T?): SetField<T?>
}

private fun stringProperty(name: String, cardinality: Int): AppSearchSchema.StringPropertyConfig =
    AppSearchSchema.StringPropertyConfig.Builder(name)
        .setCardinality(cardinality)
        .setTokenizerType(AppSearchSchema.StringPropertyConfig.TOKENIZER_NONE)
        .setIndexingType(AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_NONE)
        .setJoinableValueType(AppSearchSchema.StringPropertyConfig.JOINABLE_VALUE_TYPE_NONE)
        .build()

private fun longProperty(name: String, cardinality: Int): AppSearchSchema.LongPropertyConfig =
    AppSearchSchema.LongPropertyConfig.Builder(name)
        .setCardinality(cardinality)
        .setIndexingType(AppSearchSchema.LongPropertyConfig.INDEXING_TYPE_NONE)
        .build()

private fun doubleProperty(name: String, cardinality: Int): AppSearchSchema.DoublePropertyConfig =
    AppSearchSchema.DoublePropertyConfig.Builder(name)
        .setCardinality(cardinality)
        .build()

private fun booleanProperty(name: String, cardinality: Int): AppSearchSchema.BooleanPropertyConfig =
    AppSearchSchema.BooleanPropertyConfig.Builder(name)
        .setCardinality(cardinality)
        .build()

private fun bytesProperty(name: String, cardinality: Int): AppSearchSchema.BytesPropertyConfig =
    AppSearchSchema.BytesPropertyConfig.Builder(name)
        .setCardinality(cardinality)
        .build()

private fun docProperty(
    name: String,
    schemaName: String,
    cardinality: Int,
): AppSearchSchema.DocumentPropertyConfig =
    AppSearchSchema.DocumentPropertyConfig.Builder(name, schemaName)
        .setCardinality(cardinality)
        .setShouldIndexNestedProperties(false)
        .build()

data class Uri(
    val namespace: String = "",
    val id: String = "",
    val uri: String,
) {
    constructor(androidUri: android.net.Uri) : this(
        namespace = "",
        id = "",
        uri = androidUri.toString(),
    )

    companion object : SetFieldCompanion<Uri> {
        override fun setField(value: Uri): SetUriField = SetUriField(value = value)
        override fun setListField(value: List<Uri>): SetUriListField = SetUriListField(value = value)
        override fun setNullableField(value: Uri?): SetUriNullableField = SetUriNullableField(value = value)
    }
}

class `$$__AppSearch__Uri` : DocumentClassFactory<Uri> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("uri", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()

    override fun toGenericDocument(document: Uri): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyString("uri", document.uri)
            .build()

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): Uri = Uri(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        uri = genericDoc.getPropertyString("uri") ?: "",
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.Uri"
    }
}

data class Attachment(
    val namespace: String = "",
    val id: String = "",
    val displayName: String,
    val mimeType: String? = null,
    val uri: Uri,
) {
    companion object : SetFieldCompanion<Attachment> {
        override fun setField(value: Attachment): SetAttachmentField =
            SetAttachmentField(value = value)

        override fun setListField(value: List<Attachment>): SetAttachmentListField =
            SetAttachmentListField(value = value)

        override fun setNullableField(value: Attachment?): SetAttachmentNullableField =
            SetAttachmentNullableField(value = value)
    }
}

class `$$__AppSearch__Attachment` : DocumentClassFactory<Attachment> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("displayName", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(stringProperty("mimeType", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(
                docProperty("uri", `$$__AppSearch__Uri`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)
            )
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(Uri::class.java)

    override fun toGenericDocument(document: Attachment): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        builder.setPropertyString("displayName", document.displayName)
        document.mimeType?.let { builder.setPropertyString("mimeType", it) }
        builder.setPropertyDocument("uri", GenericDocument.fromDocumentClass(document.uri))
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): Attachment {
        val uriDoc = genericDoc.getPropertyDocument("uri")
            ?: throw IllegalStateException("Missing uri property")
        return Attachment(
            namespace = genericDoc.namespace,
            id = genericDoc.id,
            displayName = genericDoc.getPropertyString("displayName") ?: "",
            mimeType = genericDoc.getPropertyString("mimeType"),
            uri = uriDoc.toDocumentClass(Uri::class.java, mappingContext),
        )
    }

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.Attachment"
    }
}

data class Date(
    val namespace: String = "",
    val id: String = "",
    val year: Int,
    val month: Int,
    val day: Int,
)

class `$$__AppSearch__Date` : DocumentClassFactory<Date> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(longProperty("year", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(longProperty("month", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(longProperty("day", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()

    override fun toGenericDocument(document: Date): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyLong("year", document.year.toLong())
            .setPropertyLong("month", document.month.toLong())
            .setPropertyLong("day", document.day.toLong())
            .build()

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): Date = Date(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        year = genericDoc.getPropertyLong("year").toInt(),
        month = genericDoc.getPropertyLong("month").toInt(),
        day = genericDoc.getPropertyLong("day").toInt(),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.Date"
    }
}

data class TimeOfDay(
    val namespace: String = "",
    val id: String = "",
    val hours: Int,
    val minutes: Int,
    val seconds: Int = 0,
    val nanos: Int = 0,
) {
    companion object : SetFieldCompanion<TimeOfDay> {
        override fun setField(value: TimeOfDay): SetTimeOfDayField = SetTimeOfDayField(value = value)
        override fun setListField(value: List<TimeOfDay>): SetTimeOfDayListField = SetTimeOfDayListField(value = value)
        override fun setNullableField(value: TimeOfDay?): SetTimeOfDayNullableField =
            SetTimeOfDayNullableField(value = value)
    }
}

class `$$__AppSearch__TimeOfDay` : DocumentClassFactory<TimeOfDay> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(longProperty("hours", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(longProperty("minutes", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(longProperty("seconds", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(longProperty("nanos", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()

    override fun toGenericDocument(document: TimeOfDay): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyLong("hours", document.hours.toLong())
            .setPropertyLong("minutes", document.minutes.toLong())
            .setPropertyLong("seconds", document.seconds.toLong())
            .setPropertyLong("nanos", document.nanos.toLong())
            .build()

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): TimeOfDay = TimeOfDay(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        hours = genericDoc.getPropertyLong("hours").toInt(),
        minutes = genericDoc.getPropertyLong("minutes").toInt(),
        seconds = genericDoc.getPropertyLong("seconds").toInt(),
        nanos = genericDoc.getPropertyLong("nanos").toInt(),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.TimeOfDay"
    }
}

data class DateTime(
    val namespace: String = "",
    val id: String = "",
    val timeZone: String,
    val date: Date,
    val timeOfDay: TimeOfDay,
) {
    companion object : SetFieldCompanion<DateTime> {
        override fun setField(value: DateTime): SetDateTimeField = SetDateTimeField(value = value)
        override fun setListField(value: List<DateTime>): SetDateTimeListField = SetDateTimeListField(value = value)
        override fun setNullableField(value: DateTime?): SetDateTimeNullableField =
            SetDateTimeNullableField(value = value)
    }
}

class `$$__AppSearch__DateTime` : DocumentClassFactory<DateTime> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("timeZone", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(
                docProperty("date", `$$__AppSearch__Date`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)
            )
            .addProperty(
                docProperty(
                    "timeOfDay",
                    `$$__AppSearch__TimeOfDay`.SCHEMA_NAME,
                    AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED,
                )
            )
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(
        Date::class.java,
        TimeOfDay::class.java,
    )

    override fun toGenericDocument(document: DateTime): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyString("timeZone", document.timeZone)
            .setPropertyDocument("date", GenericDocument.fromDocumentClass(document.date))
            .setPropertyDocument("timeOfDay", GenericDocument.fromDocumentClass(document.timeOfDay))
            .build()

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): DateTime {
        val dateDoc = genericDoc.getPropertyDocument("date")
            ?: throw IllegalStateException("Missing date property")
        val todDoc = genericDoc.getPropertyDocument("timeOfDay")
            ?: throw IllegalStateException("Missing timeOfDay property")
        return DateTime(
            namespace = genericDoc.namespace,
            id = genericDoc.id,
            timeZone = genericDoc.getPropertyString("timeZone") ?: "",
            date = dateDoc.toDocumentClass(Date::class.java, mappingContext),
            timeOfDay = todDoc.toDocumentClass(TimeOfDay::class.java, mappingContext),
        )
    }

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.DateTime"
    }
}

data class TimeOfDayRange(
    val namespace: String = "",
    val id: String = "",
    val startTime: TimeOfDay? = null,
    val endTime: TimeOfDay? = null,
) {
    constructor() : this("", "", null, null)
}

class `$$__AppSearch__TimeOfDayRange` : DocumentClassFactory<TimeOfDayRange> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(
                docProperty(
                    "startTime",
                    `$$__AppSearch__TimeOfDay`.SCHEMA_NAME,
                    AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL,
                )
            )
            .addProperty(
                docProperty(
                    "endTime",
                    `$$__AppSearch__TimeOfDay`.SCHEMA_NAME,
                    AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL,
                )
            )
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(TimeOfDay::class.java)

    override fun toGenericDocument(document: TimeOfDayRange): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.startTime?.let { builder.setPropertyDocument("startTime", GenericDocument.fromDocumentClass(it)) }
        document.endTime?.let { builder.setPropertyDocument("endTime", GenericDocument.fromDocumentClass(it)) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): TimeOfDayRange = TimeOfDayRange(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        startTime = genericDoc.getPropertyDocument("startTime")?.toDocumentClass(TimeOfDay::class.java, mappingContext),
        endTime = genericDoc.getPropertyDocument("endTime")?.toDocumentClass(TimeOfDay::class.java, mappingContext),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.TimeOfDayRange"
    }
}

object BooleanField : SetFieldCompanion<Boolean> {
    override fun setField(value: Boolean): SetBooleanField = SetBooleanField(value = value)
    override fun setListField(value: List<Boolean>): SetBooleanListField = SetBooleanListField(value = value)
    override fun setNullableField(value: Boolean?): SetBooleanNullableField = SetBooleanNullableField(value = value)
}

data class SetBooleanField(val namespace: String = "", val id: String = "", override val value: Boolean) : SetField<Boolean>
data class SetBooleanListField(val namespace: String = "", val id: String = "", override val value: List<Boolean> = emptyList()) : SetField<List<Boolean>>
data class SetBooleanNullableField(val namespace: String = "", val id: String = "", override val value: Boolean? = null) : SetField<Boolean?>

class `$$__AppSearch__SetBooleanField` : DocumentClassFactory<SetBooleanField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(booleanProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetBooleanField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyBoolean("value", document.value).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetBooleanField =
        SetBooleanField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyBoolean("value"))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetBooleanField" }
}

class `$$__AppSearch__SetBooleanListField` : DocumentClassFactory<SetBooleanListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(booleanProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetBooleanListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyBoolean("value", *document.value.toBooleanArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetBooleanListField =
        SetBooleanListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyBooleanArray("value")?.toList().orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetBooleanListField" }
}

class `$$__AppSearch__SetBooleanNullableField` : DocumentClassFactory<SetBooleanNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(booleanProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetBooleanNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyBoolean("value", it) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetBooleanNullableField =
        SetBooleanNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyBooleanArray("value")?.firstOrNull())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetBooleanNullableField" }
}

object ByteArrayField : SetFieldCompanion<ByteArray> {
    override fun setField(value: ByteArray): SetByteArrayField = SetByteArrayField(value = value)
    override fun setListField(value: List<ByteArray>): SetByteArrayListField = SetByteArrayListField(value = value)
    override fun setNullableField(value: ByteArray?): SetByteArrayNullableField = SetByteArrayNullableField(value = value)
}

data class SetByteArrayField(val namespace: String = "", val id: String = "", override val value: ByteArray) : SetField<ByteArray>
data class SetByteArrayListField(val namespace: String = "", val id: String = "", override val value: List<ByteArray> = emptyList()) : SetField<List<ByteArray>>
data class SetByteArrayNullableField(val namespace: String = "", val id: String = "", override val value: ByteArray? = null) : SetField<ByteArray?>

class `$$__AppSearch__SetByteArrayField` : DocumentClassFactory<SetByteArrayField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(bytesProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetByteArrayField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyBytes("value", document.value).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetByteArrayField =
        SetByteArrayField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyBytes("value") ?: ByteArray(0))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetByteArrayField" }
}

class `$$__AppSearch__SetByteArrayListField` : DocumentClassFactory<SetByteArrayListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(bytesProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetByteArrayListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyBytes("value", *document.value.toTypedArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetByteArrayListField =
        SetByteArrayListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyBytesArray("value")?.toList().orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetByteArrayListField" }
}

class `$$__AppSearch__SetByteArrayNullableField` : DocumentClassFactory<SetByteArrayNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(bytesProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetByteArrayNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyBytes("value", it) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetByteArrayNullableField =
        SetByteArrayNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyBytesArray("value")?.firstOrNull())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetByteArrayNullableField" }
}

object DoubleField : SetFieldCompanion<Double> {
    override fun setField(value: Double): SetDoubleField = SetDoubleField(value = value)
    override fun setListField(value: List<Double>): SetDoubleListField = SetDoubleListField(value = value)
    override fun setNullableField(value: Double?): SetDoubleNullableField = SetDoubleNullableField(value = value)
}

data class SetDoubleField(val namespace: String = "", val id: String = "", override val value: Double) : SetField<Double>
data class SetDoubleListField(val namespace: String = "", val id: String = "", override val value: List<Double> = emptyList()) : SetField<List<Double>>
data class SetDoubleNullableField(val namespace: String = "", val id: String = "", override val value: Double? = null) : SetField<Double?>

class `$$__AppSearch__SetDoubleField` : DocumentClassFactory<SetDoubleField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(doubleProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetDoubleField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyDouble("value", document.value).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDoubleField =
        SetDoubleField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDouble("value"))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetDoubleField" }
}

class `$$__AppSearch__SetDoubleListField` : DocumentClassFactory<SetDoubleListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(doubleProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetDoubleListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyDouble("value", *document.value.toDoubleArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDoubleListField =
        SetDoubleListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDoubleArray("value")?.toList().orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetDoubleListField" }
}

class `$$__AppSearch__SetDoubleNullableField` : DocumentClassFactory<SetDoubleNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(doubleProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetDoubleNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyDouble("value", it) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDoubleNullableField =
        SetDoubleNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDoubleArray("value")?.firstOrNull())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetDoubleNullableField" }
}

object FloatField : SetFieldCompanion<Float> {
    override fun setField(value: Float): SetFloatField = SetFloatField(value = value)
    override fun setListField(value: List<Float>): SetFloatListField = SetFloatListField(value = value)
    override fun setNullableField(value: Float?): SetFloatNullableField = SetFloatNullableField(value = value)
}

data class SetFloatField(val namespace: String = "", val id: String = "", override val value: Float) : SetField<Float>
data class SetFloatListField(val namespace: String = "", val id: String = "", override val value: List<Float> = emptyList()) : SetField<List<Float>>
data class SetFloatNullableField(val namespace: String = "", val id: String = "", override val value: Float? = null) : SetField<Float?>

class `$$__AppSearch__SetFloatField` : DocumentClassFactory<SetFloatField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(doubleProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetFloatField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyDouble("value", document.value.toDouble()).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetFloatField =
        SetFloatField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDouble("value").toFloat())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetFloatField" }
}

class `$$__AppSearch__SetFloatListField` : DocumentClassFactory<SetFloatListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(doubleProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetFloatListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyDouble("value", *DoubleArray(document.value.size) { document.value[it].toDouble() })
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetFloatListField =
        SetFloatListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDoubleArray("value")?.map { it.toFloat() }.orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetFloatListField" }
}

class `$$__AppSearch__SetFloatNullableField` : DocumentClassFactory<SetFloatNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(doubleProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetFloatNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyDouble("value", it.toDouble()) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetFloatNullableField =
        SetFloatNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDoubleArray("value")?.firstOrNull()?.toFloat())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetFloatNullableField" }
}

object IntField : SetFieldCompanion<Int> {
    override fun setField(value: Int): SetIntField = SetIntField(value = value)
    override fun setListField(value: List<Int>): SetIntListField = SetIntListField(value = value)
    override fun setNullableField(value: Int?): SetIntNullableField = SetIntNullableField(value = value)
}

data class SetIntField(val namespace: String = "", val id: String = "", override val value: Int) : SetField<Int>
data class SetIntListField(val namespace: String = "", val id: String = "", override val value: List<Int> = emptyList()) : SetField<List<Int>>
data class SetIntNullableField(val namespace: String = "", val id: String = "", override val value: Int? = null) : SetField<Int?>

class `$$__AppSearch__SetIntField` : DocumentClassFactory<SetIntField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(longProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetIntField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyLong("value", document.value.toLong()).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetIntField =
        SetIntField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyLong("value").toInt())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetIntField" }
}

class `$$__AppSearch__SetIntListField` : DocumentClassFactory<SetIntListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(longProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetIntListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyLong("value", *LongArray(document.value.size) { document.value[it].toLong() })
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetIntListField =
        SetIntListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyLongArray("value")?.map { it.toInt() }.orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetIntListField" }
}

class `$$__AppSearch__SetIntNullableField` : DocumentClassFactory<SetIntNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(longProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetIntNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyLong("value", it.toLong()) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetIntNullableField =
        SetIntNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyLongArray("value")?.firstOrNull()?.toInt())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetIntNullableField" }
}

object LongField : SetFieldCompanion<Long> {
    override fun setField(value: Long): SetLongField = SetLongField(value = value)
    override fun setListField(value: List<Long>): SetLongListField = SetLongListField(value = value)
    override fun setNullableField(value: Long?): SetLongNullableField = SetLongNullableField(value = value)
}

data class SetLongField(val namespace: String = "", val id: String = "", override val value: Long) : SetField<Long>
data class SetLongListField(val namespace: String = "", val id: String = "", override val value: List<Long> = emptyList()) : SetField<List<Long>>
data class SetLongNullableField(val namespace: String = "", val id: String = "", override val value: Long? = null) : SetField<Long?>

class `$$__AppSearch__SetLongField` : DocumentClassFactory<SetLongField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(longProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetLongField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyLong("value", document.value).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetLongField =
        SetLongField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyLong("value"))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetLongField" }
}

class `$$__AppSearch__SetLongListField` : DocumentClassFactory<SetLongListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(longProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetLongListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyLong("value", *document.value.toLongArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetLongListField =
        SetLongListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyLongArray("value")?.toList().orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetLongListField" }
}

class `$$__AppSearch__SetLongNullableField` : DocumentClassFactory<SetLongNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(longProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetLongNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyLong("value", it) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetLongNullableField =
        SetLongNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyLongArray("value")?.firstOrNull())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetLongNullableField" }
}

object StringField : SetFieldCompanion<String> {
    override fun setField(value: String): SetStringField = SetStringField(value = value)
    override fun setListField(value: List<String>): SetStringListField = SetStringListField(value = value)
    override fun setNullableField(value: String?): SetStringNullableField = SetStringNullableField(value = value)
}

data class SetStringField(val namespace: String = "", val id: String = "", override val value: String) : SetField<String>
data class SetStringListField(val namespace: String = "", val id: String = "", override val value: List<String> = emptyList()) : SetField<List<String>>
data class SetStringNullableField(val namespace: String = "", val id: String = "", override val value: String? = null) : SetField<String?>

class `$$__AppSearch__SetStringField` : DocumentClassFactory<SetStringField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(stringProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetStringField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyString("value", document.value).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetStringField =
        SetStringField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyString("value") ?: "")
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetStringField" }
}

class `$$__AppSearch__SetStringListField` : DocumentClassFactory<SetStringListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(stringProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetStringListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyString("value", *document.value.toTypedArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetStringListField =
        SetStringListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyStringArray("value")?.toList().orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetStringListField" }
}

class `$$__AppSearch__SetStringNullableField` : DocumentClassFactory<SetStringNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(stringProperty("value", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()
    override fun toGenericDocument(document: SetStringNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyString("value", it) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetStringNullableField =
        SetStringNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyString("value"))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetStringNullableField" }
}

data class SetAttachmentField(val namespace: String = "", val id: String = "", override val value: Attachment) : SetField<Attachment>
data class SetAttachmentListField(val namespace: String = "", val id: String = "", override val value: List<Attachment> = emptyList()) : SetField<List<Attachment>>
data class SetAttachmentNullableField(val namespace: String = "", val id: String = "", override val value: Attachment? = null) : SetField<Attachment?>

class `$$__AppSearch__SetAttachmentField` : DocumentClassFactory<SetAttachmentField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__Attachment`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(Attachment::class.java)
    override fun toGenericDocument(document: SetAttachmentField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyDocument("value", GenericDocument.fromDocumentClass(document.value)).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetAttachmentField {
        val d = genericDoc.getPropertyDocument("value") ?: throw IllegalStateException("Missing value property")
        return SetAttachmentField(genericDoc.namespace, genericDoc.id, d.toDocumentClass(Attachment::class.java, mappingContext))
    }
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetAttachmentField" }
}

class `$$__AppSearch__SetAttachmentListField` : DocumentClassFactory<SetAttachmentListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__Attachment`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(Attachment::class.java)
    override fun toGenericDocument(document: SetAttachmentListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyDocument("value", *document.value.map { GenericDocument.fromDocumentClass(it) }.toTypedArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetAttachmentListField =
        SetAttachmentListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocumentArray("value")?.map { it.toDocumentClass(Attachment::class.java, mappingContext) }.orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetAttachmentListField" }
}

class `$$__AppSearch__SetAttachmentNullableField` : DocumentClassFactory<SetAttachmentNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__Attachment`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(Attachment::class.java)
    override fun toGenericDocument(document: SetAttachmentNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyDocument("value", GenericDocument.fromDocumentClass(it)) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetAttachmentNullableField =
        SetAttachmentNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocument("value")?.toDocumentClass(Attachment::class.java, mappingContext))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetAttachmentNullableField" }
}

data class SetDateTimeField(val namespace: String = "", val id: String = "", override val value: DateTime) : SetField<DateTime>
data class SetDateTimeListField(val namespace: String = "", val id: String = "", override val value: List<DateTime> = emptyList()) : SetField<List<DateTime>>
data class SetDateTimeNullableField(val namespace: String = "", val id: String = "", override val value: DateTime? = null) : SetField<DateTime?>

class `$$__AppSearch__SetDateTimeField` : DocumentClassFactory<SetDateTimeField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__DateTime`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(DateTime::class.java)
    override fun toGenericDocument(document: SetDateTimeField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyDocument("value", GenericDocument.fromDocumentClass(document.value)).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDateTimeField {
        val d = genericDoc.getPropertyDocument("value") ?: throw IllegalStateException("Missing value property")
        return SetDateTimeField(genericDoc.namespace, genericDoc.id, d.toDocumentClass(DateTime::class.java, mappingContext))
    }
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetDateTimeField" }
}

class `$$__AppSearch__SetDateTimeListField` : DocumentClassFactory<SetDateTimeListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__DateTime`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(DateTime::class.java)
    override fun toGenericDocument(document: SetDateTimeListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyDocument("value", *document.value.map { GenericDocument.fromDocumentClass(it) }.toTypedArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDateTimeListField =
        SetDateTimeListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocumentArray("value")?.map { it.toDocumentClass(DateTime::class.java, mappingContext) }.orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetDateTimeListField" }
}

class `$$__AppSearch__SetDateTimeNullableField` : DocumentClassFactory<SetDateTimeNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__DateTime`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(DateTime::class.java)
    override fun toGenericDocument(document: SetDateTimeNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyDocument("value", GenericDocument.fromDocumentClass(it)) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDateTimeNullableField =
        SetDateTimeNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocument("value")?.toDocumentClass(DateTime::class.java, mappingContext))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetDateTimeNullableField" }
}

data class SetTimeOfDayField(val namespace: String = "", val id: String = "", override val value: TimeOfDay) : SetField<TimeOfDay>
data class SetTimeOfDayListField(val namespace: String = "", val id: String = "", override val value: List<TimeOfDay> = emptyList()) : SetField<List<TimeOfDay>>
data class SetTimeOfDayNullableField(val namespace: String = "", val id: String = "", override val value: TimeOfDay? = null) : SetField<TimeOfDay?>

class `$$__AppSearch__SetTimeOfDayField` : DocumentClassFactory<SetTimeOfDayField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__TimeOfDay`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(TimeOfDay::class.java)
    override fun toGenericDocument(document: SetTimeOfDayField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyDocument("value", GenericDocument.fromDocumentClass(document.value)).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetTimeOfDayField {
        val d = genericDoc.getPropertyDocument("value") ?: throw IllegalStateException("Missing value property")
        return SetTimeOfDayField(genericDoc.namespace, genericDoc.id, d.toDocumentClass(TimeOfDay::class.java, mappingContext))
    }
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetTimeOfDayField" }
}

class `$$__AppSearch__SetTimeOfDayListField` : DocumentClassFactory<SetTimeOfDayListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__TimeOfDay`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(TimeOfDay::class.java)
    override fun toGenericDocument(document: SetTimeOfDayListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyDocument("value", *document.value.map { GenericDocument.fromDocumentClass(it) }.toTypedArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetTimeOfDayListField =
        SetTimeOfDayListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocumentArray("value")?.map { it.toDocumentClass(TimeOfDay::class.java, mappingContext) }.orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetTimeOfDayListField" }
}

class `$$__AppSearch__SetTimeOfDayNullableField` : DocumentClassFactory<SetTimeOfDayNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__TimeOfDay`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(TimeOfDay::class.java)
    override fun toGenericDocument(document: SetTimeOfDayNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyDocument("value", GenericDocument.fromDocumentClass(it)) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetTimeOfDayNullableField =
        SetTimeOfDayNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocument("value")?.toDocumentClass(TimeOfDay::class.java, mappingContext))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetTimeOfDayNullableField" }
}

data class SetUriField(val namespace: String = "", val id: String = "", override val value: Uri) : SetField<Uri>
data class SetUriListField(val namespace: String = "", val id: String = "", override val value: List<Uri> = emptyList()) : SetField<List<Uri>>
data class SetUriNullableField(val namespace: String = "", val id: String = "", override val value: Uri? = null) : SetField<Uri?>

class `$$__AppSearch__SetUriField` : DocumentClassFactory<SetUriField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__Uri`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(Uri::class.java)
    override fun toGenericDocument(document: SetUriField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyDocument("value", GenericDocument.fromDocumentClass(document.value)).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetUriField {
        val d = genericDoc.getPropertyDocument("value") ?: throw IllegalStateException("Missing value property")
        return SetUriField(genericDoc.namespace, genericDoc.id, d.toDocumentClass(Uri::class.java, mappingContext))
    }
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetUriField" }
}

class `$$__AppSearch__SetUriListField` : DocumentClassFactory<SetUriListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__Uri`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(Uri::class.java)
    override fun toGenericDocument(document: SetUriListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyDocument("value", *document.value.map { GenericDocument.fromDocumentClass(it) }.toTypedArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetUriListField =
        SetUriListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocumentArray("value")?.map { it.toDocumentClass(Uri::class.java, mappingContext) }.orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetUriListField" }
}

class `$$__AppSearch__SetUriNullableField` : DocumentClassFactory<SetUriNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__Uri`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(Uri::class.java)
    override fun toGenericDocument(document: SetUriNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyDocument("value", GenericDocument.fromDocumentClass(it)) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetUriNullableField =
        SetUriNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocument("value")?.toDocumentClass(Uri::class.java, mappingContext))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.types.SetUriNullableField" }
}

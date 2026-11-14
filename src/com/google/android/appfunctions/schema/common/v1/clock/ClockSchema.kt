@file:JvmName("ClockKt")

package com.google.android.appfunctions.schema.common.v1.clock

import android.app.PendingIntent
import androidx.appsearch.app.AppSearchSchema
import androidx.appsearch.app.DocumentClassFactory
import androidx.appsearch.app.DocumentClassMappingContext
import androidx.appsearch.app.GenericDocument
import com.google.android.appfunctions.AppFunctionContext
import com.google.android.appfunctions.schema.common.AppFunctionSchemaDefinition
import com.google.android.appfunctions.schema.common.v1.types.`$$__AppSearch__Date`
import com.google.android.appfunctions.schema.common.v1.types.`$$__AppSearch__DateTime`
import com.google.android.appfunctions.schema.common.v1.types.`$$__AppSearch__SetLongField`
import com.google.android.appfunctions.schema.common.v1.types.`$$__AppSearch__SetStringField`
import com.google.android.appfunctions.schema.common.v1.types.`$$__AppSearch__SetStringNullableField`
import com.google.android.appfunctions.schema.common.v1.types.`$$__AppSearch__SetTimeOfDayField`
import com.google.android.appfunctions.schema.common.v1.types.`$$__AppSearch__TimeOfDay`
import com.google.android.appfunctions.schema.common.v1.types.Date
import com.google.android.appfunctions.schema.common.v1.types.DateTime
import com.google.android.appfunctions.schema.common.v1.types.SetField
import com.google.android.appfunctions.schema.common.v1.types.SetFieldCompanion
import com.google.android.appfunctions.schema.common.v1.types.SetLongField
import com.google.android.appfunctions.schema.common.v1.types.SetStringField
import com.google.android.appfunctions.schema.common.v1.types.SetStringNullableField
import com.google.android.appfunctions.schema.common.v1.types.SetTimeOfDayField
import com.google.android.appfunctions.schema.common.v1.types.TimeOfDay

const val CLOCK_CATEGORY = "clock"

@Retention(AnnotationRetention.SOURCE)
annotation class AlarmStatus {
    companion object {
        const val UNKNOWN = "UNKNOWN"
        const val SCHEDULED = "SCHEDULED"
        const val FIRING = "FIRING"
        const val SNOOZED = "SNOOZED"
        const val DISMISSED = "DISMISSED"
        const val MISSED = "MISSED"
    }
}

@Retention(AnnotationRetention.SOURCE)
annotation class AlarmSortingOrder {
    companion object {
        const val START_TIME_ASCENDING = "START_TIME_ASCENDING"
        const val START_TIME_DESCENDING = "START_TIME_DESCENDING"
    }
}

@Retention(AnnotationRetention.SOURCE)
annotation class TimerState {
    companion object {
        const val UNKNOWN = "UNKNOWN"
        const val RESET = "RESET"
        const val STARTED = "STARTED"
        const val PAUSED = "PAUSED"
        const val EXPIRED = "EXPIRED"
        const val MISSED = "MISSED"
    }
}

@Retention(AnnotationRetention.SOURCE)
annotation class TimerSortingOrder {
    companion object {
        const val EXPIRE_TIME_ASCENDING = "EXPIRE_TIME_ASCENDING"
        const val EXPIRE_TIME_DESCENDING = "EXPIRE_TIME_DESCENDING"
    }
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

private fun booleanProperty(name: String, cardinality: Int): AppSearchSchema.BooleanPropertyConfig =
    AppSearchSchema.BooleanPropertyConfig.Builder(name)
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

data class DayPattern(
    val namespace: String = "",
    val id: String = "",
    val date: Date? = null,
    val recurrencePattern: String? = null,
) {
    constructor() : this("", "", null, null)

    companion object : SetFieldCompanion<DayPattern> {
        override fun setField(value: DayPattern): SetDayPatternField =
            SetDayPatternField(value = value)

        override fun setListField(value: List<DayPattern>): SetDayPatternListField =
            SetDayPatternListField(value = value)

        override fun setNullableField(value: DayPattern?): SetDayPatternNullableField =
            SetDayPatternNullableField(value = value)
    }
}

class `$$__AppSearch__DayPattern` : DocumentClassFactory<DayPattern> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(docProperty("date", `$$__AppSearch__Date`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(stringProperty("recurrencePattern", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(Date::class.java)

    override fun toGenericDocument(document: DayPattern): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.date?.let { builder.setPropertyDocument("date", GenericDocument.fromDocumentClass(it)) }
        document.recurrencePattern?.let { builder.setPropertyString("recurrencePattern", it) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): DayPattern = DayPattern(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        date = genericDoc.getPropertyDocument("date")?.toDocumentClass(Date::class.java, mappingContext),
        recurrencePattern = genericDoc.getPropertyString("recurrencePattern"),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.DayPattern"
    }
}

data class SetDayPatternField(val namespace: String = "", val id: String = "", override val value: DayPattern) : SetField<DayPattern>
data class SetDayPatternListField(val namespace: String = "", val id: String = "", override val value: List<DayPattern> = emptyList()) : SetField<List<DayPattern>>
data class SetDayPatternNullableField(val namespace: String = "", val id: String = "", override val value: DayPattern? = null) : SetField<DayPattern?>

class `$$__AppSearch__SetDayPatternField` : DocumentClassFactory<SetDayPatternField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__DayPattern`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(DayPattern::class.java)
    override fun toGenericDocument(document: SetDayPatternField): GenericDocument =
        GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
            .setPropertyDocument("value", GenericDocument.fromDocumentClass(document.value)).build()
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDayPatternField {
        val d = genericDoc.getPropertyDocument("value") ?: throw IllegalStateException("Missing value property")
        return SetDayPatternField(genericDoc.namespace, genericDoc.id, d.toDocumentClass(DayPattern::class.java, mappingContext))
    }
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.SetDayPatternField" }
}

class `$$__AppSearch__SetDayPatternListField` : DocumentClassFactory<SetDayPatternListField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__DayPattern`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REPEATED)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(DayPattern::class.java)
    override fun toGenericDocument(document: SetDayPatternListField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        if (document.value.isNotEmpty()) b.setPropertyDocument("value", *document.value.map { GenericDocument.fromDocumentClass(it) }.toTypedArray())
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDayPatternListField =
        SetDayPatternListField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocumentArray("value")?.map { it.toDocumentClass(DayPattern::class.java, mappingContext) }.orEmpty())
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.SetDayPatternListField" }
}

class `$$__AppSearch__SetDayPatternNullableField` : DocumentClassFactory<SetDayPatternNullableField> {
    override fun getSchemaName(): String = SCHEMA_NAME
    override fun getSchema(): AppSearchSchema = AppSearchSchema.Builder(SCHEMA_NAME)
        .addProperty(docProperty("value", `$$__AppSearch__DayPattern`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL)).build()
    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(DayPattern::class.java)
    override fun toGenericDocument(document: SetDayPatternNullableField): GenericDocument {
        val b = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.value?.let { b.setPropertyDocument("value", GenericDocument.fromDocumentClass(it)) }
        return b.build()
    }
    override fun fromGenericDocument(genericDoc: GenericDocument, mappingContext: DocumentClassMappingContext): SetDayPatternNullableField =
        SetDayPatternNullableField(genericDoc.namespace, genericDoc.id, genericDoc.getPropertyDocument("value")?.toDocumentClass(DayPattern::class.java, mappingContext))
    companion object { const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.SetDayPatternNullableField" }
}

data class Alarm(
    val namespace: String = "",
    val id: String = "",
    val label: String? = null,
    val time: TimeOfDay,
    val alarmStatus: String? = null,
    val dayPattern: DayPattern? = null,
)

class `$$__AppSearch__Alarm` : DocumentClassFactory<Alarm> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("label", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("time", `$$__AppSearch__TimeOfDay`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(stringProperty("alarmStatus", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("dayPattern", `$$__AppSearch__DayPattern`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(
        TimeOfDay::class.java,
        DayPattern::class.java,
    )

    override fun toGenericDocument(document: Alarm): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.label?.let { builder.setPropertyString("label", it) }
        builder.setPropertyDocument("time", GenericDocument.fromDocumentClass(document.time))
        document.alarmStatus?.let { builder.setPropertyString("alarmStatus", it) }
        document.dayPattern?.let { builder.setPropertyDocument("dayPattern", GenericDocument.fromDocumentClass(it)) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): Alarm {
        val timeDoc = genericDoc.getPropertyDocument("time")
            ?: throw IllegalStateException("Missing time property")
        return Alarm(
            namespace = genericDoc.namespace,
            id = genericDoc.id,
            label = genericDoc.getPropertyString("label"),
            time = timeDoc.toDocumentClass(TimeOfDay::class.java, mappingContext),
            alarmStatus = genericDoc.getPropertyString("alarmStatus"),
            dayPattern = genericDoc.getPropertyDocument("dayPattern")?.toDocumentClass(DayPattern::class.java, mappingContext),
        )
    }

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.Alarm"
    }
}

data class CreateAlarmParams(
    val namespace: String = "",
    val id: String = "",
    val label: String? = null,
    val time: TimeOfDay,
    val alarmStatus: String? = null,
    val dayPattern: DayPattern? = null,
    val externalId: String? = null,
)

class `$$__AppSearch__CreateAlarmParams` : DocumentClassFactory<CreateAlarmParams> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("label", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("time", `$$__AppSearch__TimeOfDay`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(stringProperty("alarmStatus", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("dayPattern", `$$__AppSearch__DayPattern`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(stringProperty("externalId", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(
        TimeOfDay::class.java,
        DayPattern::class.java,
    )

    override fun toGenericDocument(document: CreateAlarmParams): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.label?.let { builder.setPropertyString("label", it) }
        builder.setPropertyDocument("time", GenericDocument.fromDocumentClass(document.time))
        document.alarmStatus?.let { builder.setPropertyString("alarmStatus", it) }
        document.dayPattern?.let { builder.setPropertyDocument("dayPattern", GenericDocument.fromDocumentClass(it)) }
        document.externalId?.let { builder.setPropertyString("externalId", it) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): CreateAlarmParams {
        val timeDoc = genericDoc.getPropertyDocument("time")
            ?: throw IllegalStateException("Missing time property")
        return CreateAlarmParams(
            namespace = genericDoc.namespace,
            id = genericDoc.id,
            label = genericDoc.getPropertyString("label"),
            time = timeDoc.toDocumentClass(TimeOfDay::class.java, mappingContext),
            alarmStatus = genericDoc.getPropertyString("alarmStatus"),
            dayPattern = genericDoc.getPropertyDocument("dayPattern")?.toDocumentClass(DayPattern::class.java, mappingContext),
            externalId = genericDoc.getPropertyString("externalId"),
        )
    }

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.CreateAlarmParams"
    }
}

data class UpdateAlarmParams(
    val namespace: String = "",
    val id: String = "",
    val alarmId: String,
    val label: SetStringNullableField? = null,
    val time: SetTimeOfDayField? = null,
    val alarmStatus: SetStringNullableField? = null,
    val dayPattern: SetDayPatternNullableField? = null,
)

class `$$__AppSearch__UpdateAlarmParams` : DocumentClassFactory<UpdateAlarmParams> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("alarmId", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(docProperty("label", `$$__AppSearch__SetStringNullableField`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("time", `$$__AppSearch__SetTimeOfDayField`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("alarmStatus", `$$__AppSearch__SetStringNullableField`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("dayPattern", `$$__AppSearch__SetDayPatternNullableField`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(
        SetStringNullableField::class.java,
        SetTimeOfDayField::class.java,
        SetDayPatternNullableField::class.java,
    )

    override fun toGenericDocument(document: UpdateAlarmParams): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        builder.setPropertyString("alarmId", document.alarmId)
        document.label?.let { builder.setPropertyDocument("label", GenericDocument.fromDocumentClass(it)) }
        document.time?.let { builder.setPropertyDocument("time", GenericDocument.fromDocumentClass(it)) }
        document.alarmStatus?.let { builder.setPropertyDocument("alarmStatus", GenericDocument.fromDocumentClass(it)) }
        document.dayPattern?.let { builder.setPropertyDocument("dayPattern", GenericDocument.fromDocumentClass(it)) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): UpdateAlarmParams = UpdateAlarmParams(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        alarmId = genericDoc.getPropertyString("alarmId") ?: "",
        label = genericDoc.getPropertyDocument("label")?.toDocumentClass(SetStringNullableField::class.java, mappingContext),
        time = genericDoc.getPropertyDocument("time")?.toDocumentClass(SetTimeOfDayField::class.java, mappingContext),
        alarmStatus = genericDoc.getPropertyDocument("alarmStatus")?.toDocumentClass(SetStringNullableField::class.java, mappingContext),
        dayPattern = genericDoc.getPropertyDocument("dayPattern")?.toDocumentClass(SetDayPatternNullableField::class.java, mappingContext),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.UpdateAlarmParams"
    }
}

data class FindAlarmsParams(
    val namespace: String = "",
    val id: String = "",
    val startTime: DateTime? = null,
    val endTime: DateTime? = null,
    val label: String? = null,
    val alarmStatus: String? = null,
    val dayPattern: DayPattern? = null,
    val maxCount: Int = 0,
    val sortingOrder: String? = null,
)

class `$$__AppSearch__FindAlarmsParams` : DocumentClassFactory<FindAlarmsParams> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(docProperty("startTime", `$$__AppSearch__DateTime`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("endTime", `$$__AppSearch__DateTime`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(stringProperty("label", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(stringProperty("alarmStatus", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("dayPattern", `$$__AppSearch__DayPattern`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(longProperty("maxCount", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(stringProperty("sortingOrder", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(
        DateTime::class.java,
        DayPattern::class.java,
    )

    override fun toGenericDocument(document: FindAlarmsParams): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.startTime?.let { builder.setPropertyDocument("startTime", GenericDocument.fromDocumentClass(it)) }
        document.endTime?.let { builder.setPropertyDocument("endTime", GenericDocument.fromDocumentClass(it)) }
        document.label?.let { builder.setPropertyString("label", it) }
        document.alarmStatus?.let { builder.setPropertyString("alarmStatus", it) }
        document.dayPattern?.let { builder.setPropertyDocument("dayPattern", GenericDocument.fromDocumentClass(it)) }
        builder.setPropertyLong("maxCount", document.maxCount.toLong())
        document.sortingOrder?.let { builder.setPropertyString("sortingOrder", it) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): FindAlarmsParams = FindAlarmsParams(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        startTime = genericDoc.getPropertyDocument("startTime")?.toDocumentClass(DateTime::class.java, mappingContext),
        endTime = genericDoc.getPropertyDocument("endTime")?.toDocumentClass(DateTime::class.java, mappingContext),
        label = genericDoc.getPropertyString("label"),
        alarmStatus = genericDoc.getPropertyString("alarmStatus"),
        dayPattern = genericDoc.getPropertyDocument("dayPattern")?.toDocumentClass(DayPattern::class.java, mappingContext),
        maxCount = genericDoc.getPropertyLong("maxCount").toInt(),
        sortingOrder = genericDoc.getPropertyString("sortingOrder"),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.FindAlarmsParams"
    }
}

data class Timer(
    val namespace: String = "",
    val id: String = "",
    val label: String? = null,
    val originalDurationMillis: Long = 0L,
    val durationMillis: Long = 0L,
    val timerState: String? = null,
    val remainingDurationMillis: Long = 0L,
    val expireTimeEpochMillis: Long? = null,
    val isFiring: Boolean? = null,
)

class `$$__AppSearch__Timer` : DocumentClassFactory<Timer> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("label", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(longProperty("originalDurationMillis", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(longProperty("durationMillis", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(stringProperty("timerState", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(longProperty("remainingDurationMillis", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(longProperty("expireTimeEpochMillis", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(booleanProperty("isFiring", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()

    override fun toGenericDocument(document: Timer): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.label?.let { builder.setPropertyString("label", it) }
        builder.setPropertyLong("originalDurationMillis", document.originalDurationMillis)
        builder.setPropertyLong("durationMillis", document.durationMillis)
        document.timerState?.let { builder.setPropertyString("timerState", it) }
        builder.setPropertyLong("remainingDurationMillis", document.remainingDurationMillis)
        document.expireTimeEpochMillis?.let { builder.setPropertyLong("expireTimeEpochMillis", it) }
        document.isFiring?.let { builder.setPropertyBoolean("isFiring", it) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): Timer = Timer(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        label = genericDoc.getPropertyString("label"),
        originalDurationMillis = genericDoc.getPropertyLong("originalDurationMillis"),
        durationMillis = genericDoc.getPropertyLong("durationMillis"),
        timerState = genericDoc.getPropertyString("timerState"),
        remainingDurationMillis = genericDoc.getPropertyLong("remainingDurationMillis"),
        expireTimeEpochMillis = genericDoc.getPropertyLongArray("expireTimeEpochMillis")?.firstOrNull(),
        isFiring = genericDoc.getPropertyBooleanArray("isFiring")?.firstOrNull(),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.Timer"
    }
}

data class CreateTimerParams(
    val namespace: String = "",
    val id: String = "",
    val label: String? = null,
    val originalDurationMillis: Long = 0L,
    val timerState: String? = null,
    val externalId: String? = null,
)

class `$$__AppSearch__CreateTimerParams` : DocumentClassFactory<CreateTimerParams> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("label", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(longProperty("originalDurationMillis", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(stringProperty("timerState", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(stringProperty("externalId", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()

    override fun toGenericDocument(document: CreateTimerParams): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.label?.let { builder.setPropertyString("label", it) }
        builder.setPropertyLong("originalDurationMillis", document.originalDurationMillis)
        document.timerState?.let { builder.setPropertyString("timerState", it) }
        document.externalId?.let { builder.setPropertyString("externalId", it) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): CreateTimerParams = CreateTimerParams(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        label = genericDoc.getPropertyString("label"),
        originalDurationMillis = genericDoc.getPropertyLong("originalDurationMillis"),
        timerState = genericDoc.getPropertyString("timerState"),
        externalId = genericDoc.getPropertyString("externalId"),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.CreateTimerParams"
    }
}

data class UpdateTimerParams(
    val namespace: String = "",
    val id: String = "",
    val timerId: String,
    val label: SetStringNullableField? = null,
    val durationMs: SetLongField? = null,
    val timerState: SetStringField? = null,
)

class `$$__AppSearch__UpdateTimerParams` : DocumentClassFactory<UpdateTimerParams> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("timerId", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(docProperty("label", `$$__AppSearch__SetStringNullableField`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("durationMs", `$$__AppSearch__SetLongField`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(docProperty("timerState", `$$__AppSearch__SetStringField`.SCHEMA_NAME, AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = listOf(
        SetStringNullableField::class.java,
        SetLongField::class.java,
        SetStringField::class.java,
    )

    override fun toGenericDocument(document: UpdateTimerParams): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        builder.setPropertyString("timerId", document.timerId)
        document.label?.let { builder.setPropertyDocument("label", GenericDocument.fromDocumentClass(it)) }
        document.durationMs?.let { builder.setPropertyDocument("durationMs", GenericDocument.fromDocumentClass(it)) }
        document.timerState?.let { builder.setPropertyDocument("timerState", GenericDocument.fromDocumentClass(it)) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): UpdateTimerParams = UpdateTimerParams(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        timerId = genericDoc.getPropertyString("timerId") ?: "",
        label = genericDoc.getPropertyDocument("label")?.toDocumentClass(SetStringNullableField::class.java, mappingContext),
        durationMs = genericDoc.getPropertyDocument("durationMs")?.toDocumentClass(SetLongField::class.java, mappingContext),
        timerState = genericDoc.getPropertyDocument("timerState")?.toDocumentClass(SetStringField::class.java, mappingContext),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.UpdateTimerParams"
    }
}

data class FindTimersParams(
    val namespace: String = "",
    val id: String = "",
    val label: String? = null,
    val timerState: String? = null,
    val maxCount: Int = 0,
    val sortingOrder: String? = null,
)

class `$$__AppSearch__FindTimersParams` : DocumentClassFactory<FindTimersParams> {
    override fun getSchemaName(): String = SCHEMA_NAME

    override fun getSchema(): AppSearchSchema =
        AppSearchSchema.Builder(SCHEMA_NAME)
            .addProperty(stringProperty("label", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(stringProperty("timerState", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .addProperty(longProperty("maxCount", AppSearchSchema.PropertyConfig.CARDINALITY_REQUIRED))
            .addProperty(stringProperty("sortingOrder", AppSearchSchema.PropertyConfig.CARDINALITY_OPTIONAL))
            .build()

    override fun getDependencyDocumentClasses(): List<Class<*>> = emptyList()

    override fun toGenericDocument(document: FindTimersParams): GenericDocument {
        val builder = GenericDocument.Builder<GenericDocument.Builder<*>>(document.namespace, document.id, SCHEMA_NAME)
        document.label?.let { builder.setPropertyString("label", it) }
        document.timerState?.let { builder.setPropertyString("timerState", it) }
        builder.setPropertyLong("maxCount", document.maxCount.toLong())
        document.sortingOrder?.let { builder.setPropertyString("sortingOrder", it) }
        return builder.build()
    }

    override fun fromGenericDocument(
        genericDoc: GenericDocument,
        mappingContext: DocumentClassMappingContext,
    ): FindTimersParams = FindTimersParams(
        namespace = genericDoc.namespace,
        id = genericDoc.id,
        label = genericDoc.getPropertyString("label"),
        timerState = genericDoc.getPropertyString("timerState"),
        maxCount = genericDoc.getPropertyLong("maxCount").toInt(),
        sortingOrder = genericDoc.getPropertyString("sortingOrder"),
    )

    companion object {
        const val SCHEMA_NAME = "com.google.android.appfunctions.schema.common.v1.clock.FindTimersParams"
    }
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "createAlarm", version = 1)
interface CreateAlarm {
    suspend fun createAlarm(appFunctionContext: AppFunctionContext, createAlarmParams: CreateAlarmParams): Alarm
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "createAlarms", version = 1)
interface CreateAlarms {
    suspend fun createAlarms(appFunctionContext: AppFunctionContext, createAlarmsParams: List<CreateAlarmParams>): List<Alarm>
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "showAlarm", version = 1)
interface ShowAlarm {
    suspend fun showAlarm(appFunctionContext: AppFunctionContext, alarmId: String): PendingIntent
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "updateAlarm", version = 1)
interface UpdateAlarm {
    suspend fun updateAlarm(appFunctionContext: AppFunctionContext, updateAlarmParams: UpdateAlarmParams): Alarm
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "deleteAlarms", version = 1)
interface DeleteAlarms {
    suspend fun deleteAlarms(appFunctionContext: AppFunctionContext, alarmIds: List<String>): List<String>
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "getAlarms", version = 1)
interface GetAlarms {
    suspend fun getAlarms(appFunctionContext: AppFunctionContext, alarmIds: List<String>): List<Alarm>
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "findAlarms", version = 1)
interface FindAlarms {
    suspend fun findAlarms(appFunctionContext: AppFunctionContext, findAlarmsParams: FindAlarmsParams): List<Alarm>
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "createTimer", version = 1)
interface CreateTimer {
    suspend fun createTimer(appFunctionContext: AppFunctionContext, createTimerParams: CreateTimerParams): Timer
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "createTimers", version = 1)
interface CreateTimers {
    suspend fun createTimers(appFunctionContext: AppFunctionContext, createTimersParams: List<CreateTimerParams>): List<Timer>
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "showTimer", version = 1)
interface ShowTimer {
    suspend fun showTimer(appFunctionContext: AppFunctionContext, timerId: String): PendingIntent
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "updateTimer", version = 1)
interface UpdateTimer {
    suspend fun updateTimer(appFunctionContext: AppFunctionContext, updateTimerParams: UpdateTimerParams): Timer
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "deleteTimers", version = 1)
interface DeleteTimers {
    suspend fun deleteTimers(appFunctionContext: AppFunctionContext, timerIds: List<String>): List<String>
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "getTimers", version = 1)
interface GetTimers {
    suspend fun getTimers(appFunctionContext: AppFunctionContext, timerIds: List<String>): List<Timer>
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "findTimers", version = 1)
interface FindTimers {
    suspend fun findTimers(appFunctionContext: AppFunctionContext, findTimersParams: FindTimersParams): List<Timer>
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "startStopwatch", version = 1)
interface StartStopwatch {
    suspend fun startStopwatch(appFunctionContext: AppFunctionContext)
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "stopStopwatch", version = 1)
interface StopStopwatch {
    suspend fun stopStopwatch(appFunctionContext: AppFunctionContext)
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "resetStopwatch", version = 1)
interface ResetStopwatch {
    suspend fun resetStopwatch(appFunctionContext: AppFunctionContext)
}

@AppFunctionSchemaDefinition(category = CLOCK_CATEGORY, name = "showStopwatch", version = 1)
interface ShowStopwatch {
    suspend fun showStopwatch(appFunctionContext: AppFunctionContext): PendingIntent
}

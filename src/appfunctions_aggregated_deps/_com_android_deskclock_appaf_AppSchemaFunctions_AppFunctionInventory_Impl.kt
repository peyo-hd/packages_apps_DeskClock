package appfunctions_aggregated_deps

import com.android.deskclock.appaf.ClockFunctions_createAlarm
import com.android.deskclock.appaf.ClockFunctions_createTimer
import com.android.deskclock.appaf.ClockFunctions_deleteAlarms
import com.android.deskclock.appaf.ClockFunctions_deleteTimers
import com.android.deskclock.appaf.ClockFunctions_findAlarms
import com.android.deskclock.appaf.ClockFunctions_findTimers
import com.android.deskclock.appaf.ClockFunctions_showAlarm
import com.android.deskclock.appaf.ClockFunctions_showTimer
import com.android.deskclock.appaf.ClockFunctions_updateAlarm
import com.android.deskclock.appaf.ClockFunctions_updateTimer
import com.google.android.appfunctions.AppFunctionDataTypeMetadata
import com.google.android.appfunctions.AppFunctionMetadata
import com.google.android.appfunctions.AppFunctionParameterMetadata
import com.google.android.appfunctions.internal.AppFunctionInventory

class _com_android_deskclock_appaf_AppSchemaFunctions_AppFunctionInventory_Impl :
    AppFunctionInventory {

    private companion object {
        const val NAMESPACE = "androidx.appsearch.functions"
    }

    private val CLOCKFUNCTIONS_CREATEALARM_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "createAlarmParams",
        namespace = NAMESPACE,
        name = "createAlarmParams",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_createAlarm#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.CreateAlarmParams",
        ),
    )

    private val CLOCKFUNCTIONS_CREATEALARM_PARAMETERS = listOf(
        CLOCKFUNCTIONS_CREATEALARM_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_CREATEALARM = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_createAlarm",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_CREATEALARM_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_createAlarm#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.Alarm",
        ),
    )

    private val CLOCKFUNCTIONS_SHOWALARM_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "alarmId",
        namespace = NAMESPACE,
        name = "alarmId",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_showAlarm#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_STRING,
            isList = false,
            isNullable = false,
            documentType = null,
        ),
    )

    private val CLOCKFUNCTIONS_SHOWALARM_PARAMETERS = listOf(
        CLOCKFUNCTIONS_SHOWALARM_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_SHOWALARM = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_showAlarm",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_SHOWALARM_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_showAlarm#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_PENDING_INTENT,
            isList = false,
            isNullable = false,
            documentType = null,
        ),
    )

    private val CLOCKFUNCTIONS_UPDATEALARM_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "updateAlarmParams",
        namespace = NAMESPACE,
        name = "updateAlarmParams",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_updateAlarm#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.UpdateAlarmParams",
        ),
    )

    private val CLOCKFUNCTIONS_UPDATEALARM_PARAMETERS = listOf(
        CLOCKFUNCTIONS_UPDATEALARM_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_UPDATEALARM = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_updateAlarm",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_UPDATEALARM_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_updateAlarm#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.Alarm",
        ),
    )

    private val CLOCKFUNCTIONS_DELETEALARMS_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "alarmIds",
        namespace = NAMESPACE,
        name = "alarmIds",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_deleteAlarms#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_STRING,
            isList = true,
            isNullable = false,
            documentType = null,
        ),
    )

    private val CLOCKFUNCTIONS_DELETEALARMS_PARAMETERS = listOf(
        CLOCKFUNCTIONS_DELETEALARMS_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_DELETEALARMS = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_deleteAlarms",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_DELETEALARMS_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_deleteAlarms#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_STRING,
            isList = true,
            isNullable = false,
            documentType = null,
        ),
    )

    private val CLOCKFUNCTIONS_CREATETIMER_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "createTimerParams",
        namespace = NAMESPACE,
        name = "createTimerParams",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_createTimer#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.CreateTimerParams",
        ),
    )

    private val CLOCKFUNCTIONS_CREATETIMER_PARAMETERS = listOf(
        CLOCKFUNCTIONS_CREATETIMER_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_CREATETIMER = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_createTimer",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_CREATETIMER_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_createTimer#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.Timer",
        ),
    )

    private val CLOCKFUNCTIONS_SHOWTIMER_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "timerId",
        namespace = NAMESPACE,
        name = "timerId",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_showTimer#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_STRING,
            isList = false,
            isNullable = false,
            documentType = null,
        ),
    )

    private val CLOCKFUNCTIONS_SHOWTIMER_PARAMETERS = listOf(
        CLOCKFUNCTIONS_SHOWTIMER_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_SHOWTIMER = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_showTimer",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_SHOWTIMER_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_showTimer#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_PENDING_INTENT,
            isList = false,
            isNullable = false,
            documentType = null,
        ),
    )

    private val CLOCKFUNCTIONS_DELETETIMERS_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "timerIds",
        namespace = NAMESPACE,
        name = "timerIds",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_deleteTimers#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_STRING,
            isList = true,
            isNullable = false,
            documentType = null,
        ),
    )

    private val CLOCKFUNCTIONS_DELETETIMERS_PARAMETERS = listOf(
        CLOCKFUNCTIONS_DELETETIMERS_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_DELETETIMERS = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_deleteTimers",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_DELETETIMERS_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_deleteTimers#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_STRING,
            isList = true,
            isNullable = false,
            documentType = null,
        ),
    )

    private val CLOCKFUNCTIONS_UPDATETIMER_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "updateTimerParams",
        namespace = NAMESPACE,
        name = "updateTimerParams",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_updateTimer#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.UpdateTimerParams",
        ),
    )

    private val CLOCKFUNCTIONS_UPDATETIMER_PARAMETERS = listOf(
        CLOCKFUNCTIONS_UPDATETIMER_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_UPDATETIMER = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_updateTimer",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_UPDATETIMER_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_updateTimer#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.Timer",
        ),
    )

    private val CLOCKFUNCTIONS_FINDALARMS_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "findAlarmsParams",
        namespace = NAMESPACE,
        name = "findAlarmsParams",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_findAlarms#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.FindAlarmsParams",
        ),
    )

    private val CLOCKFUNCTIONS_FINDALARMS_PARAMETERS = listOf(
        CLOCKFUNCTIONS_FINDALARMS_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_FINDALARMS = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_findAlarms",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_FINDALARMS_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_findAlarms#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = true,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.Alarm",
        ),
    )

    private val CLOCKFUNCTIONS_FINDTIMERS_PARAMETER_1 = AppFunctionParameterMetadata(
        id = "findTimersParams",
        namespace = NAMESPACE,
        name = "findTimersParams",
        description = "",
        parameterType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_findTimers#PARAMETER_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = false,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.FindTimersParams",
        ),
    )

    private val CLOCKFUNCTIONS_FINDTIMERS_PARAMETERS = listOf(
        CLOCKFUNCTIONS_FINDTIMERS_PARAMETER_1,
    )

    private val CLOCKFUNCTIONS_FINDTIMERS = AppFunctionMetadata(
        id = "com.android.deskclock.appaf#ClockFunctions_findTimers",
        namespace = NAMESPACE,
        enabledByDefault = true,
        description = "",
        parameters = CLOCKFUNCTIONS_FINDTIMERS_PARAMETERS,
        returnType = AppFunctionDataTypeMetadata(
            id = "com.android.deskclock.appaf#ClockFunctions_findTimers#RETURN_TYPE",
            namespace = NAMESPACE,
            dataType = AppFunctionDataTypeMetadata.DATA_TYPE_OBJECT,
            isList = true,
            isNullable = false,
            documentType = "com.google.android.appfunctions.schema.common.v1.clock.Timer",
        ),
    )

    override val functionToMetadataMap: Map<Function<*>, AppFunctionMetadata> = mapOf(
        ::ClockFunctions_createAlarm to CLOCKFUNCTIONS_CREATEALARM,
        ::ClockFunctions_showAlarm to CLOCKFUNCTIONS_SHOWALARM,
        ::ClockFunctions_updateAlarm to CLOCKFUNCTIONS_UPDATEALARM,
        ::ClockFunctions_deleteAlarms to CLOCKFUNCTIONS_DELETEALARMS,
        ::ClockFunctions_createTimer to CLOCKFUNCTIONS_CREATETIMER,
        ::ClockFunctions_showTimer to CLOCKFUNCTIONS_SHOWTIMER,
        ::ClockFunctions_deleteTimers to CLOCKFUNCTIONS_DELETETIMERS,
        ::ClockFunctions_updateTimer to CLOCKFUNCTIONS_UPDATETIMER,
        ::ClockFunctions_findAlarms to CLOCKFUNCTIONS_FINDALARMS,
        ::ClockFunctions_findTimers to CLOCKFUNCTIONS_FINDTIMERS,
    )
}

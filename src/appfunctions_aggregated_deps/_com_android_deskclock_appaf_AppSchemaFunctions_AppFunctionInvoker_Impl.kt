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
import com.google.android.appfunctions.AppFunctionContext
import com.google.android.appfunctions.internal.AppFunctionInvoker
import com.google.android.appfunctions.schema.common.v1.clock.CreateAlarmParams
import com.google.android.appfunctions.schema.common.v1.clock.CreateTimerParams
import com.google.android.appfunctions.schema.common.v1.clock.FindAlarmsParams
import com.google.android.appfunctions.schema.common.v1.clock.FindTimersParams
import com.google.android.appfunctions.schema.common.v1.clock.UpdateAlarmParams
import com.google.android.appfunctions.schema.common.v1.clock.UpdateTimerParams

class _com_android_deskclock_appaf_AppSchemaFunctions_AppFunctionInvoker_Impl :
    AppFunctionInvoker {

    override val supportedFunctionIds: Set<String> = setOf(
        "com.android.deskclock.appaf#ClockFunctions_createAlarm",
        "com.android.deskclock.appaf#ClockFunctions_showAlarm",
        "com.android.deskclock.appaf#ClockFunctions_updateAlarm",
        "com.android.deskclock.appaf#ClockFunctions_deleteAlarms",
        "com.android.deskclock.appaf#ClockFunctions_createTimer",
        "com.android.deskclock.appaf#ClockFunctions_showTimer",
        "com.android.deskclock.appaf#ClockFunctions_deleteTimers",
        "com.android.deskclock.appaf#ClockFunctions_updateTimer",
        "com.android.deskclock.appaf#ClockFunctions_findAlarms",
        "com.android.deskclock.appaf#ClockFunctions_findTimers",
    )

    @Suppress("UNCHECKED_CAST")
    override suspend fun unsafeInvoke(
        appFunctionContext: AppFunctionContext,
        functionIdentifier: String,
        parameters: Map<String, Any?>,
    ): Any? = when (functionIdentifier) {
        "com.android.deskclock.appaf#ClockFunctions_createAlarm" ->
            ClockFunctions_createAlarm(
                appFunctionContext,
                parameters["createAlarmParams"] as CreateAlarmParams,
            )
        "com.android.deskclock.appaf#ClockFunctions_showAlarm" ->
            ClockFunctions_showAlarm(
                appFunctionContext,
                parameters["alarmId"] as String,
            )
        "com.android.deskclock.appaf#ClockFunctions_updateAlarm" ->
            ClockFunctions_updateAlarm(
                appFunctionContext,
                parameters["updateAlarmParams"] as UpdateAlarmParams,
            )
        "com.android.deskclock.appaf#ClockFunctions_deleteAlarms" ->
            ClockFunctions_deleteAlarms(
                appFunctionContext,
                parameters["alarmIds"] as List<String>,
            )
        "com.android.deskclock.appaf#ClockFunctions_createTimer" ->
            ClockFunctions_createTimer(
                appFunctionContext,
                parameters["createTimerParams"] as CreateTimerParams,
            )
        "com.android.deskclock.appaf#ClockFunctions_showTimer" ->
            ClockFunctions_showTimer(
                appFunctionContext,
                parameters["timerId"] as String,
            )
        "com.android.deskclock.appaf#ClockFunctions_deleteTimers" ->
            ClockFunctions_deleteTimers(
                appFunctionContext,
                parameters["timerIds"] as List<String>,
            )
        "com.android.deskclock.appaf#ClockFunctions_updateTimer" ->
            ClockFunctions_updateTimer(
                appFunctionContext,
                parameters["updateTimerParams"] as UpdateTimerParams,
            )
        "com.android.deskclock.appaf#ClockFunctions_findAlarms" ->
            ClockFunctions_findAlarms(
                appFunctionContext,
                parameters["findAlarmsParams"] as FindAlarmsParams,
            )
        "com.android.deskclock.appaf#ClockFunctions_findTimers" ->
            ClockFunctions_findTimers(
                appFunctionContext,
                parameters["findTimersParams"] as FindTimersParams,
            )
        else -> throw IllegalArgumentException("Unknown functionIdentifier: $functionIdentifier")
    }
}

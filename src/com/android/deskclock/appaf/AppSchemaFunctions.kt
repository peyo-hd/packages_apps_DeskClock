@file:JvmName("AppSchemaFunctionsKt")

package com.android.deskclock.appaf

import android.app.PendingIntent
import com.google.android.appfunctions.AppFunctionContext
import com.google.android.appfunctions.schema.app.AppFunctionSchema
import com.google.android.appfunctions.schema.app.AppSchemaImpl
import com.google.android.appfunctions.schema.common.v1.clock.Alarm
import com.google.android.appfunctions.schema.common.v1.clock.CLOCK_CATEGORY
import com.google.android.appfunctions.schema.common.v1.clock.CreateAlarmParams
import com.google.android.appfunctions.schema.common.v1.clock.CreateTimerParams
import com.google.android.appfunctions.schema.common.v1.clock.FindAlarmsParams
import com.google.android.appfunctions.schema.common.v1.clock.FindTimersParams
import com.google.android.appfunctions.schema.common.v1.clock.Timer
import com.google.android.appfunctions.schema.common.v1.clock.UpdateAlarmParams
import com.google.android.appfunctions.schema.common.v1.clock.UpdateTimerParams

private val clockFunctions by lazy { ClockFunctions() }

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "createAlarm", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_createAlarm(
    appFunctionContext: AppFunctionContext,
    createAlarmParams: CreateAlarmParams,
): Alarm = clockFunctions.createAlarm(appFunctionContext, createAlarmParams)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "showAlarm", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_showAlarm(
    appFunctionContext: AppFunctionContext,
    alarmId: String,
): PendingIntent = clockFunctions.showAlarm(appFunctionContext, alarmId)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "updateAlarm", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_updateAlarm(
    appFunctionContext: AppFunctionContext,
    updateAlarmParams: UpdateAlarmParams,
): Alarm = clockFunctions.updateAlarm(appFunctionContext, updateAlarmParams)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "deleteAlarms", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_deleteAlarms(
    appFunctionContext: AppFunctionContext,
    alarmIds: List<String>,
): List<String> = clockFunctions.deleteAlarms(appFunctionContext, alarmIds)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "createTimer", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_createTimer(
    appFunctionContext: AppFunctionContext,
    createTimerParams: CreateTimerParams,
): Timer = clockFunctions.createTimer(appFunctionContext, createTimerParams)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "showTimer", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_showTimer(
    appFunctionContext: AppFunctionContext,
    timerId: String,
): PendingIntent = clockFunctions.showTimer(appFunctionContext, timerId)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "deleteTimers", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_deleteTimers(
    appFunctionContext: AppFunctionContext,
    timerIds: List<String>,
): List<String> = clockFunctions.deleteTimers(appFunctionContext, timerIds)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "updateTimer", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_updateTimer(
    appFunctionContext: AppFunctionContext,
    updateTimerParams: UpdateTimerParams,
): Timer = clockFunctions.updateTimer(appFunctionContext, updateTimerParams)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "findAlarms", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_findAlarms(
    appFunctionContext: AppFunctionContext,
    findAlarmsParams: FindAlarmsParams,
): List<Alarm> = clockFunctions.findAlarms(appFunctionContext, findAlarmsParams)

@AppFunctionSchema(category = CLOCK_CATEGORY, name = "findTimers", version = 1)
@AppSchemaImpl
suspend fun ClockFunctions_findTimers(
    appFunctionContext: AppFunctionContext,
    findTimersParams: FindTimersParams,
): List<Timer> = clockFunctions.findTimers(appFunctionContext, findTimersParams)

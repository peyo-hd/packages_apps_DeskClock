package com.android.deskclock.appaf

import android.app.PendingIntent
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import com.android.deskclock.DeskClock
import com.android.deskclock.R
import com.android.deskclock.alarms.AlarmUpdateHandler
import com.android.deskclock.data.DataModel
import com.android.deskclock.data.Timer
import com.android.deskclock.data.Weekdays
import com.android.deskclock.provider.Alarm
import com.android.deskclock.provider.ClockContract
import com.android.deskclock.uidata.UiDataModel
import com.google.android.appfunctions.AppFunctionContext
import com.google.android.appfunctions.AppFunctionException
import com.google.android.appfunctions.schema.common.v1.clock.AlarmStatus
import com.google.android.appfunctions.schema.common.v1.clock.CreateAlarm
import com.google.android.appfunctions.schema.common.v1.clock.CreateAlarmParams
import com.google.android.appfunctions.schema.common.v1.clock.CreateTimer
import com.google.android.appfunctions.schema.common.v1.clock.CreateTimerParams
import com.google.android.appfunctions.schema.common.v1.clock.DayPattern
import com.google.android.appfunctions.schema.common.v1.clock.DeleteAlarms
import com.google.android.appfunctions.schema.common.v1.clock.DeleteTimers
import com.google.android.appfunctions.schema.common.v1.clock.FindAlarms
import com.google.android.appfunctions.schema.common.v1.clock.FindAlarmsParams
import com.google.android.appfunctions.schema.common.v1.clock.FindTimers
import com.google.android.appfunctions.schema.common.v1.clock.FindTimersParams
import com.google.android.appfunctions.schema.common.v1.clock.ShowAlarm
import com.google.android.appfunctions.schema.common.v1.clock.ShowTimer
import com.google.android.appfunctions.schema.common.v1.clock.TimerState
import com.google.android.appfunctions.schema.common.v1.clock.UpdateAlarm
import com.google.android.appfunctions.schema.common.v1.clock.UpdateAlarmParams
import com.google.android.appfunctions.schema.common.v1.clock.UpdateTimer
import com.google.android.appfunctions.schema.common.v1.clock.UpdateTimerParams
import com.google.android.appfunctions.schema.common.v1.types.TimeOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import com.google.android.appfunctions.schema.common.v1.clock.Alarm as SchemaAlarm
import com.google.android.appfunctions.schema.common.v1.clock.Timer as SchemaTimer

class ClockFunctions :
    CreateAlarm,
    ShowAlarm,
    UpdateAlarm,
    DeleteAlarms,
    CreateTimer,
    ShowTimer,
    UpdateTimer,
    DeleteTimers,
    FindAlarms,
    FindTimers {

    override suspend fun createAlarm(
        appFunctionContext: AppFunctionContext,
        createAlarmParams: CreateAlarmParams,
    ): SchemaAlarm {
        val context = appFunctionContext.context
        val cr = context.contentResolver
        val daysOfWeek = parseDaysOfWeek(createAlarmParams.dayPattern)
        val enabled = createAlarmParams.alarmStatus != AlarmStatus.DISMISSED
        val newAlarm = Alarm(createAlarmParams.time.hours, createAlarmParams.time.minutes).apply {
            label = createAlarmParams.label.orEmpty()
            this.daysOfWeek = daysOfWeek
            this.enabled = enabled
            vibrate = true
            deleteAfterUse = !daysOfWeek.isRepeating
        }
        val inserted = Alarm.addAlarm(cr, newAlarm)
        if (enabled) {
            AlarmUpdateHandler(context, null, null).asyncAddAlarm(inserted)
        }
        AppSearchUtil.getInstance(context).addAppSearchAlarmScheduled(inserted, enabled)
        return inserted.toSchemaAlarm()
    }

    override suspend fun showAlarm(
        appFunctionContext: AppFunctionContext,
        alarmId: String,
    ): PendingIntent {
        val context = appFunctionContext.context
        val id = alarmId.toLongOrNull()
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_INVALID_ARGUMENT,
                "Invalid alarmId: $alarmId",
            )
        Alarm.getAlarm(context.contentResolver, id)
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_NOT_FOUND,
                "Alarm not found: $alarmId",
            )
        val intent = Intent(context, DeskClock::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        UiDataModel.uiDataModel.selectedTab = UiDataModel.Tab.ALARMS
        return PendingIntent.getActivity(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    override suspend fun updateAlarm(
        appFunctionContext: AppFunctionContext,
        updateAlarmParams: UpdateAlarmParams,
    ): SchemaAlarm {
        val context = appFunctionContext.context
        val cr = context.contentResolver
        val id = updateAlarmParams.alarmId.toLongOrNull()
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_INVALID_ARGUMENT,
                "Invalid alarmId: ${updateAlarmParams.alarmId}",
            )
        val existing = Alarm.getAlarm(cr, id)
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_NOT_FOUND,
                "Alarm not found: ${updateAlarmParams.alarmId}",
            )
        updateAlarmParams.label?.let { existing.label = it.value.orEmpty() }
        updateAlarmParams.time?.value?.let { tod ->
            existing.hour = tod.hours
            existing.minutes = tod.minutes
        }
        updateAlarmParams.alarmStatus?.value?.let { status ->
            existing.enabled = status != AlarmStatus.DISMISSED
        }
        updateAlarmParams.dayPattern?.let { patternField ->
            existing.daysOfWeek = parseDaysOfWeek(patternField.value)
        }
        AlarmUpdateHandler(context, null, null).asyncUpdateAlarm(existing, false, true)
        AppSearchUtil.getInstance(context).updateAppSearchAlarmScheduled(existing, existing.enabled)
        return existing.toSchemaAlarm()
    }

    override suspend fun deleteAlarms(
        appFunctionContext: AppFunctionContext,
        alarmIds: List<String>,
    ): List<String> {
        val context = appFunctionContext.context
        val cr = context.contentResolver
        val deleted = mutableListOf<String>()
        val handler = AlarmUpdateHandler(context, null, null)
        for (rawId in alarmIds) {
            val id = rawId.toLongOrNull() ?: continue
            val existing = Alarm.getAlarm(cr, id) ?: continue
            handler.asyncDeleteAlarm(existing)
            AppSearchUtil.getInstance(context).deleteAppSearchAlarm(id)
            deleted.add(rawId)
        }
        return deleted
    }

    override suspend fun createTimer(
        appFunctionContext: AppFunctionContext,
        createTimerParams: CreateTimerParams,
    ): SchemaTimer = withContext(Dispatchers.Main) {
        val context = appFunctionContext.context
        val length = createTimerParams.originalDurationMillis
        if (length <= 0L) {
            throw AppFunctionException(
                AppFunctionException.ERROR_INVALID_ARGUMENT,
                "originalDurationMillis must be positive",
            )
        }
        var timer = DataModel.dataModel.addTimer(length, createTimerParams.label.orEmpty(), false)
        if (createTimerParams.timerState != TimerState.RESET &&
            createTimerParams.timerState != TimerState.PAUSED
        ) {
            timer = timer.start()
            DataModel.dataModel.updateTimer(timer)
        }
        AppSearchUtil.getInstance(context).addAppSearchTimer(timer)
        timer.toSchemaTimer()
    }

    override suspend fun showTimer(
        appFunctionContext: AppFunctionContext,
        timerId: String,
    ): PendingIntent {
        val context = appFunctionContext.context
        val id = timerId.toIntOrNull()
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_INVALID_ARGUMENT,
                "Invalid timerId: $timerId",
            )
        DataModel.dataModel.getTimer(id)
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_NOT_FOUND,
                "Timer not found: $timerId",
            )
        val intent = Intent(context, DeskClock::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        UiDataModel.uiDataModel.selectedTab = UiDataModel.Tab.TIMERS
        return PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    override suspend fun updateTimer(
        appFunctionContext: AppFunctionContext,
        updateTimerParams: UpdateTimerParams,
    ): SchemaTimer = withContext(Dispatchers.Main) {
        val context = appFunctionContext.context
        val id = updateTimerParams.timerId.toIntOrNull()
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_INVALID_ARGUMENT,
                "Invalid timerId: ${updateTimerParams.timerId}",
            )
        var timer = DataModel.dataModel.getTimer(id)
            ?: throw AppFunctionException(
                AppFunctionException.ERROR_NOT_FOUND,
                "Timer not found: ${updateTimerParams.timerId}",
            )
        updateTimerParams.label?.let {
            timer = timer.setLabel(it.value.orEmpty())
        }
        updateTimerParams.durationMs?.value?.let { newDuration ->
            timer = timer.setLength(newDuration)
        }
        updateTimerParams.timerState?.value?.let { state ->
            timer = when (state) {
                TimerState.STARTED -> timer.start()
                TimerState.PAUSED -> timer.pause()
                TimerState.RESET -> timer.reset()
                TimerState.EXPIRED -> timer.expire()
                else -> timer
            }
        }
        DataModel.dataModel.updateTimer(timer)
        when (timer.state) {
            Timer.State.RUNNING -> AppSearchUtil.getInstance(context).updateAppSearchTimerToContinue(timer)
            Timer.State.PAUSED -> AppSearchUtil.getInstance(context).updateAppSearchTimerToPause(timer)
            Timer.State.EXPIRED -> AppSearchUtil.getInstance(context).updateAppSearchTimerToExpired(timer)
            else -> AppSearchUtil.getInstance(context).addAppSearchTimer(timer)
        }
        timer.toSchemaTimer()
    }

    override suspend fun deleteTimers(
        appFunctionContext: AppFunctionContext,
        timerIds: List<String>,
    ): List<String> = withContext(Dispatchers.Main) {
        val context = appFunctionContext.context
        val deleted = mutableListOf<String>()
        for (rawId in timerIds) {
            val id = rawId.toIntOrNull() ?: continue
            val timer = DataModel.dataModel.getTimer(id) ?: continue
            DataModel.dataModel.removeTimer(timer)
            AppSearchUtil.getInstance(context).deleteAppSearchTimer(id)
            deleted.add(rawId)
        }
        deleted
    }

    override suspend fun findAlarms(
        appFunctionContext: AppFunctionContext,
        findAlarmsParams: FindAlarmsParams,
    ): List<SchemaAlarm> {
        val alarms = Alarm.getAlarms(appFunctionContext.context.contentResolver, null)
        val filtered = alarms.filter { alarm ->
            val labelMatches = findAlarmsParams.label.isNullOrEmpty() ||
                alarm.label.orEmpty().contains(findAlarmsParams.label, ignoreCase = true)
            val statusMatches = findAlarmsParams.alarmStatus.isNullOrEmpty() ||
                (if (alarm.enabled) AlarmStatus.SCHEDULED else AlarmStatus.DISMISSED) == findAlarmsParams.alarmStatus
            labelMatches && statusMatches
        }
        val mapped = filtered.map { it.toSchemaAlarm() }
        return if (findAlarmsParams.maxCount > 0) mapped.take(findAlarmsParams.maxCount) else mapped
    }

    override suspend fun findTimers(
        appFunctionContext: AppFunctionContext,
        findTimersParams: FindTimersParams,
    ): List<SchemaTimer> = withContext(Dispatchers.Main) {
        val timers = DataModel.dataModel.timers
        val filtered = timers.filter { timer ->
            val labelMatches = findTimersParams.label.isNullOrEmpty() ||
                timer.label.orEmpty().contains(findTimersParams.label, ignoreCase = true)
            val stateMatches = findTimersParams.timerState.isNullOrEmpty() ||
                timer.state.toSchemaState() == findTimersParams.timerState
            labelMatches && stateMatches
        }
        val mapped = filtered.map { it.toSchemaTimer() }
        if (findTimersParams.maxCount > 0) mapped.take(findTimersParams.maxCount) else mapped
    }

    private fun parseDaysOfWeek(dayPattern: DayPattern?): Weekdays {
        if (dayPattern == null) return Weekdays.NONE
        val pattern = dayPattern.recurrencePattern?.uppercase().orEmpty()
        if (pattern.isEmpty()) return Weekdays.NONE
        val days = mutableListOf<Int>()
        if ("EVERY_DAY" in pattern || "DAILY" in pattern) {
            return Weekdays.ALL
        }
        if ("SUN" in pattern) days.add(Calendar.SUNDAY)
        if ("MON" in pattern) days.add(Calendar.MONDAY)
        if ("TUE" in pattern) days.add(Calendar.TUESDAY)
        if ("WED" in pattern) days.add(Calendar.WEDNESDAY)
        if ("THU" in pattern) days.add(Calendar.THURSDAY)
        if ("FRI" in pattern) days.add(Calendar.FRIDAY)
        if ("SAT" in pattern) days.add(Calendar.SATURDAY)
        return Weekdays.fromBits(
            days.fold(0) { acc, day -> acc or (1 shl (day - 1)) }
        )
    }

    private fun Alarm.toSchemaAlarm(): SchemaAlarm =
        SchemaAlarm(
            namespace = DataUtil.NAMESPACE,
            id = id.toString(),
            label = label,
            time = TimeOfDay(hours = hour, minutes = minutes),
            alarmStatus = if (enabled) AlarmStatus.SCHEDULED else AlarmStatus.DISMISSED,
            dayPattern = createDayPatternFromAlarm(this),
        )

    private fun createDayPatternFromAlarm(alarm: Alarm): DayPattern? {
        if (!alarm.daysOfWeek.isRepeating) return null
        val names = listOf(
            Calendar.SUNDAY to "SUNDAY",
            Calendar.MONDAY to "MONDAY",
            Calendar.TUESDAY to "TUESDAY",
            Calendar.WEDNESDAY to "WEDNESDAY",
            Calendar.THURSDAY to "THURSDAY",
            Calendar.FRIDAY to "FRIDAY",
            Calendar.SATURDAY to "SATURDAY",
        ).filter { (day, _) -> alarm.daysOfWeek.isBitOn(day) }
            .joinToString(",") { it.second }
        return DayPattern(recurrencePattern = names)
    }

    private fun Timer.toSchemaTimer(): SchemaTimer =
        SchemaTimer(
            namespace = DataUtil.NAMESPACE,
            id = id.toString(),
            label = label,
            originalDurationMillis = length,
            durationMillis = totalLength,
            timerState = state.toSchemaState(),
            remainingDurationMillis = remainingTime,
            expireTimeEpochMillis = if (isRunning) wallClockExpirationTime else null,
            isFiring = isExpired || isMissed,
        )

    private fun Timer.State.toSchemaState(): String = when (this) {
        Timer.State.RESET -> TimerState.RESET
        Timer.State.RUNNING -> TimerState.STARTED
        Timer.State.PAUSED -> TimerState.PAUSED
        Timer.State.EXPIRED -> TimerState.EXPIRED
        Timer.State.MISSED -> TimerState.MISSED
    }

    companion object {
        private const val TAG = "ClockFunctions"
    }
}

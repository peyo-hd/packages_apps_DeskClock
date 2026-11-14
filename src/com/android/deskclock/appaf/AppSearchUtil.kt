package com.android.deskclock.appaf

import android.content.Context
import android.util.Log
import androidx.appsearch.builtintypes.AlarmInstance
import com.android.deskclock.provider.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.appsearch.builtintypes.Alarm as AppSearchAlarm
import androidx.appsearch.builtintypes.Timer as AppSearchTimer
import com.android.deskclock.data.Timer as DeskClockTimer

class AppSearchUtil private constructor(context: Context) {

    private val appContext: Context = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mAlarmAppSearchManager = AlarmAppSearchManager(appContext, scope)
    private val mTimerAppSearchManager = TimerAppSearchManager(appContext, scope)

    fun insertDefaultAlarmsToAppSearch() {
        scope.launch {
            try {
                val alarms = Alarm.getAlarms(appContext.contentResolver, null)
                for (alarm in alarms) {
                    addAppSearchAlarmScheduled(alarm, alarm.enabled)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert default alarms to AppSearch", e)
            }
        }
    }

    fun addAppSearchAlarmScheduled(alarm: Alarm, enabled: Boolean) {
        addAppSearchAlarm(alarm, AlarmInstance.STATUS_SCHEDULED, enabled)
    }

    fun updateAppSearchAlarmScheduled(alarm: Alarm, enabled: Boolean) {
        updateAppSearchAlarm(alarm, AlarmInstance.STATUS_SCHEDULED, enabled)
    }

    fun updateAppSearchAlarmFiring(alarm: Alarm, enabled: Boolean) {
        updateAppSearchAlarm(alarm, AlarmInstance.STATUS_FIRING, enabled)
    }

    fun updateAppSearchAlarmSnoozed(alarm: Alarm, enabled: Boolean) {
        updateAppSearchAlarm(alarm, AlarmInstance.STATUS_SNOOZED, enabled)
    }

    fun updateAppSearchAlarmDismissed(alarm: Alarm, enabled: Boolean) {
        updateAppSearchAlarm(alarm, AlarmInstance.STATUS_DISMISSED, enabled)
    }

    fun addAppSearchAlarm(alarm: Alarm, instanceStatus: Int, enabled: Boolean) {
        scope.launch {
            try {
                val built = buildAppSearchAlarm(alarm, instanceStatus, enabled)
                mAlarmAppSearchManager.addAlarm(built)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add alarm id=${alarm.id} to AppSearch", e)
            }
        }
    }

    fun updateAppSearchAlarm(alarm: Alarm, instanceStatus: Int, enabled: Boolean) {
        addAppSearchAlarm(alarm, instanceStatus, enabled)
    }

    fun deleteAppSearchAlarm(alarmId: Long) {
        mAlarmAppSearchManager.deleteAlarms(DataUtil.NAMESPACE, alarmId.toString())
    }

    fun deleteAppSearchAlarm(alarmId: Int) {
        mAlarmAppSearchManager.deleteAlarms(DataUtil.NAMESPACE, alarmId.toString())
    }

    fun addAppSearchTimer(timer: DeskClockTimer) {
        syncTimer(timer, AppSearchTimer.STATUS_STARTED)
    }

    fun updateAppSearchTimerToContinue(timer: DeskClockTimer) {
        syncTimer(timer, AppSearchTimer.STATUS_STARTED)
    }

    fun updateAppSearchTimerToPause(timer: DeskClockTimer) {
        syncTimer(timer, AppSearchTimer.STATUS_PAUSED)
    }

    fun updateAppSearchTimerToExpired(timer: DeskClockTimer) {
        syncTimer(timer, AppSearchTimer.STATUS_EXPIRED)
    }

    fun deleteAppSearchTimer(timerId: Int) {
        mTimerAppSearchManager.deleteTimer(DataUtil.NAMESPACE, timerId.toString())
    }

    private fun syncTimer(timer: DeskClockTimer, status: Int) {
        scope.launch {
            try {
                val built = buildAppSearchTimer(timer, status)
                mTimerAppSearchManager.addTimer(built)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync timer id=${timer.id} to AppSearch", e)
            }
        }
    }

    private fun buildAppSearchAlarm(
        alarm: Alarm,
        instanceStatus: Int,
        enabled: Boolean,
    ): AppSearchAlarm {
        val builder = AppSearchAlarm.Builder(DataUtil.NAMESPACE, alarm.id.toString())
            .setEnabled(enabled)
            .setHour(alarm.hour)
            .setMinute(alarm.minutes)
        alarm.label?.let { builder.setName(it) }
        val daysList = mutableListOf<Int>()
        for (calDay in listOf(
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
        )) {
            if (alarm.daysOfWeek.isBitOn(calDay)) {
                daysList.add(calDay)
            }
        }
        if (daysList.isNotEmpty()) {
            builder.setDaysOfWeek(*listToIntArray(daysList))
        }
        val instance = DataUtil.getAlarmInstance(
            alarmId = alarm.id,
            date = null,
            hour = alarm.hour,
            minute = alarm.minutes,
            status = instanceStatus,
            instanceId = 0,
        )
        builder.setNextInstance(instance)
        return builder.build()
    }

    private fun buildAppSearchTimer(
        timer: DeskClockTimer,
        status: Int,
    ): AppSearchTimer {
        return AppSearchTimer.Builder(DataUtil.NAMESPACE, timer.id.toString())
            .setName(timer.label ?: "")
            .setOriginalDurationMillis(timer.length)
            .setDurationMillis(timer.totalLength)
            .setRemainingDurationMillis(timer.remainingTime)
            .setStatus(status)
            .build()
    }

    fun listToIntArray(list: List<Int>): IntArray {
        val arr = IntArray(list.size)
        for (i in list.indices) {
            arr[i] = list[i]
        }
        return arr
    }

    companion object {
        const val ALARM_DATA_SCHEMA = "builtin:Alarm"
        const val TIMER_DATA_SCHEMA = "builtin:Timer"
        private const val TAG = "AppSearchUtil"

        @Volatile
        private var mAppSearchUtil: AppSearchUtil? = null

        @JvmStatic
        fun getInstance(context: Context): AppSearchUtil =
            mAppSearchUtil ?: synchronized(this) {
                mAppSearchUtil ?: AppSearchUtil(context).also { mAppSearchUtil = it }
            }
    }
}

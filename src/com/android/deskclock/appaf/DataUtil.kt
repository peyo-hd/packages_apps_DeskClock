package com.android.deskclock.appaf

import androidx.appsearch.builtintypes.AlarmInstance
import com.google.android.appfunctions.schema.common.v1.types.Date
import java.util.Calendar
import java.util.Locale

object DataUtil {
    @JvmStatic
    fun getAlarmInstance(
        alarmId: Long,
        date: Date?,
        hour: Int,
        minute: Int,
        status: Int,
        instanceId: Int,
    ): AlarmInstance {
        val cal = Calendar.getInstance()
        val year = date?.year ?: cal.get(Calendar.YEAR)
        val month = (date?.month ?: (cal.get(Calendar.MONTH) + 1)) - 1
        val day = date?.day ?: cal.get(Calendar.DAY_OF_MONTH)
        val scheduledTime = String.format(
            Locale.US,
            "%04d-%02d-%02dT%02d:%02d:00",
            year,
            month + 1,
            day,
            hour,
            minute,
        )
        return AlarmInstance.Builder(
            NAMESPACE,
            "alarm_instance_${alarmId}_$instanceId",
            scheduledTime,
        )
            .setStatus(status)
            .build()
    }

    const val NAMESPACE = "deskclock"
}

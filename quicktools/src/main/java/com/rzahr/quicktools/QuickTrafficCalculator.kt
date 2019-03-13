@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools

import android.content.Context
import android.net.TrafficStats
import android.os.SystemClock
import com.rzahr.quicktools.extensions.addAsDefaultWithId
import com.rzahr.quicktools.extensions.addWithId
import com.rzahr.quicktools.extensions.rzPrefVal
import com.rzahr.quicktools.utils.QuickDateUtils
import com.rzahr.quicktools.utils.QuickUtils
import java.util.*
import java.util.concurrent.TimeUnit

object QuickTrafficCalculator {

    const val LAST_SAVED_TIME_BOOTED_KEY = "com.rzahr.quicktools.LastSavedTimeBooted"
    const val LAST_CURRENT_SAVED_UPLOAD_TRAFFIC_KEY = "com.rzahr.quicktools.LastCurrentSavedUploadTraffic"
    const val LAST_CURRENT_SAVED_DOWNLOAD_TRAFFIC_KEY = "com.rzahr.quicktools.LastCurrentSavedDownloadTraffic"
    const val TOTAL_SAVED_DOWNLOAD_TRAFFIC_KEY = "com.rzahr.quicktools.TotalSavedDownloadTraffic"
    const val TOTAL_SAVED_UPLOAD_TRAFFIC_KEY = "com.rzahr.quicktools.TotalSavedUploadTraffic"
    const val START_DATE_DATA_CONSUMPTION_KEY = "com.rzahr.quicktools.DTEMnthlyUsge"
    const val DURATION_APP_OFF_KEY = "com.rzahr.quicktools.TimeAppOff"
    const val DURATION_APP_ON_KEY = "com.rzahr.quicktools.TimeAppOn"

    fun check(context: Context) {

        QuickUtils.backgroundUpdater({

            "0".addAsDefaultWithId(DURATION_APP_ON_KEY)
            "0".addAsDefaultWithId(DURATION_APP_OFF_KEY)
            0L.addAsDefaultWithId(TOTAL_SAVED_DOWNLOAD_TRAFFIC_KEY)
            0L.addAsDefaultWithId(TOTAL_SAVED_UPLOAD_TRAFFIC_KEY)
            0L.addAsDefaultWithId(LAST_CURRENT_SAVED_DOWNLOAD_TRAFFIC_KEY)
            0L.addAsDefaultWithId(LAST_CURRENT_SAVED_UPLOAD_TRAFFIC_KEY)

            if (QuickInjectable.pref().checkSharedPrefValueIfExist(LAST_SAVED_TIME_BOOTED_KEY)) {
                val lastTimeBooted = Date()
                lastTimeBooted.time = Date().time - SystemClock.elapsedRealtime()

                QuickDateUtils.getDateString(
                    lastTimeBooted,
                    QuickDateUtils.DASHED_FORMAT
                ).addAsDefaultWithId(LAST_SAVED_TIME_BOOTED_KEY)
            }


            //the  below is used to calculate the amount of time the app was online and offline when the user was signed in AND ALSO THE TOTAL DOWNLOAD AND UPLOAD TRAFFIC
            if (START_DATE_DATA_CONSUMPTION_KEY.rzPrefVal<String>().isEmpty()) Date().time.toString().addWithId(START_DATE_DATA_CONSUMPTION_KEY)

            val dateNow = Date()
            //reset monthly timer
            if (dateNow.time - java.lang.Long.parseLong(START_DATE_DATA_CONSUMPTION_KEY.rzPrefVal<String>()) >= TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS)) {

                "0".addWithId(DURATION_APP_OFF_KEY)
                "0".addWithId(DURATION_APP_ON_KEY)
                dateNow.time.toString().addWithId(START_DATE_DATA_CONSUMPTION_KEY)
                0L.addWithId(TOTAL_SAVED_DOWNLOAD_TRAFFIC_KEY)
                0L.addWithId(TOTAL_SAVED_UPLOAD_TRAFFIC_KEY)
            }

            val lastTimeBootedDate = Date()
            lastTimeBootedDate.time = Date().time - SystemClock.elapsedRealtime()

            val dateDeviceBoot = QuickDateUtils.getDateString(lastTimeBootedDate, QuickDateUtils.DASHED_FORMAT)
            val savedDateDeviceBoot = QuickInjectable.pref().get(LAST_SAVED_TIME_BOOTED_KEY)

            try {

                //case when the device is restarted
                if (dateDeviceBoot != savedDateDeviceBoot) {

                    0L.addWithId(LAST_CURRENT_SAVED_UPLOAD_TRAFFIC_KEY)
                    // update the dateDeviceBoot in shared preference
                    dateDeviceBoot.addWithId(LAST_SAVED_TIME_BOOTED_KEY)
                }

                //traffic since boot time
                val downloadTrafficSinceBoot = TrafficStats.getUidRxBytes(context.applicationInfo.uid)
                val uploadTrafficSinceBoot = TrafficStats.getUidTxBytes(context.applicationInfo.uid)

                val totalDownloadTraffic = QuickInjectable.pref().getLong(TOTAL_SAVED_DOWNLOAD_TRAFFIC_KEY).plus(downloadTrafficSinceBoot - QuickInjectable.pref().getLong(LAST_CURRENT_SAVED_DOWNLOAD_TRAFFIC_KEY))
                val totalUploadTraffic = QuickInjectable.pref().getLong(TOTAL_SAVED_UPLOAD_TRAFFIC_KEY).plus(uploadTrafficSinceBoot - QuickInjectable.pref().getLong(LAST_CURRENT_SAVED_UPLOAD_TRAFFIC_KEY))

                totalDownloadTraffic.addWithId(TOTAL_SAVED_DOWNLOAD_TRAFFIC_KEY)
                totalUploadTraffic.addWithId(TOTAL_SAVED_UPLOAD_TRAFFIC_KEY)
                downloadTrafficSinceBoot.addWithId(LAST_CURRENT_SAVED_DOWNLOAD_TRAFFIC_KEY)
                uploadTrafficSinceBoot.addWithId(LAST_CURRENT_SAVED_UPLOAD_TRAFFIC_KEY)

                QuickLogWriter.debugLogging("Total Downloaded Traffic: " + totalDownloadTraffic * 0.001 * 0.001 + " Mb")
                QuickLogWriter.debugLogging("Total Uploaded Traffic: " + totalUploadTraffic * 0.001 * 0.001 + " Mb")
            }

            catch (e: Exception) {

                QuickLogWriter.errorLogging("Error", e.toString())
            }
        },{},{})
    }
}
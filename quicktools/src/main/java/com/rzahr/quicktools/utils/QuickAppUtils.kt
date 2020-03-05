package com.rzahr.quicktools.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresPermission
import com.rzahr.quicktools.*
import com.rzahr.quicktools.extensions.addWithId
import com.rzahr.quicktools.utils.QuickAppUtils.isOnline
import java.util.*

/**
 * @author Rashad Zahr
 *
 * various function for the application about the device
 */
@Suppress("unused")
object QuickAppUtils {

    /**
     * used in base classes to change language on demand
     * @return the ContextWrapper object
     */
    fun getWrapper(newBase: Context?): ContextWrapper? {

        return QuickContextWrapper.wrap(newBase, QuickInjectable.pref().get("Language"))
    }

    /**
     * gets if the device has wifi or 3g
     * @return if the device is connected to a wifi or 3g
     */
    @Suppress("DEPRECATION")
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isOnline(): Boolean {

        val connectivityManager = (QuickInjectable.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)|| actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
                else -> false
            }
        }
        else return if (connectivityManager.activeNetworkInfo == null) false else connectivityManager.activeNetworkInfo!!.isConnected
    }


    /**
     * gets the battery level
     * @return the battery level
     */
    fun getBatteryLevel(): Int {

        return try {

            val batteryIntent = QuickInjectable.applicationContext()
                .registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val batteryLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val batteryScale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

            if (batteryLevel == -1 || batteryScale == -1) Math.round(50f) else Math.round(batteryLevel.toFloat() / batteryScale.toFloat() * 100f)

        } catch (exc: Exception) {

            QuickLogWriter.errorLogging("Error in getBatteryLevel:", exc.toString())
            0
        }
    }

    /**
     * gets if the device is plugged in or not
     * @return boolean value representing if the device is plugged in or not
     */
    fun isPluggedIn(): Boolean {

        try {

            val plugged = QuickInjectable.applicationContext().registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.getIntExtra(
                BatteryManager.EXTRA_PLUGGED, -1) ?: -1

            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        }

        catch (exc: Exception) {

            QuickLogWriter.errorLogging("Error in isPluggedIn:", exc.toString())
        }

        return false
    }

    /**
     * gets the current language set on the device if english or arabic
     * @returns the current language symbol
     */
    fun getLanguageIdentifier(): String {

        @Suppress("DEPRECATION") val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            QuickInjectable.applicationContext().resources.configuration.locales.get(0)
        else QuickInjectable.applicationContext().resources.configuration.locale

        return if (locale.toString() == "l" || locale.toString() == "en_US" || locale.toString().contains("en", true))
            QuickVariables.ENGLISH_LANG_KEY
        else QuickVariables.ARABIC_LANG_KEY
    }

    /**
     * gets if the application is white-listed
     * @return boolean value identifying if the application is white-listed
     */
    fun isInDozeWhiteList(): Boolean? {

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) true else QuickInjectable.applicationContext().getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(
            QuickInjectable.applicationContext().packageName)
    }

    /**
     * gets if the device has power saver mode enabled
     * @return boolean value representing if the power saver is enabled
     */
    fun isPowerSaverOn(): Boolean {

        return  Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                (QuickInjectable.applicationContext().getSystemService(Context.POWER_SERVICE) as PowerManager).isPowerSaveMode
    }

    /**
     * gets if the device's screen is turned on
     * @return boolean value representing if the screen is turned on
     */
    fun isScreenOn(): Boolean {

        val powerManager = QuickInjectable.applicationContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH &&
                powerManager.isInteractive || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH &&
                powerManager.isScreenOn
    }

    /**
     * gets the device name
     * @return the device name
     */
    fun getDeviceName(): String {

        return try {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            if (model.startsWith(manufacturer)) {
                model
            } else {
                "$manufacturer $model"
            }
        } catch (ex: Exception) {
            "UNKNOWN"
        }
    }

    /**
     * gets the operating system name
     * @return the operating system name
     */
    fun getOSName(): String {

        return try {
            Build.VERSION.RELEASE
        } catch (e: Exception) {
            "Not Found"
        }
    }

    /**
     * gets the unique identifier of the device
     * @return the unique identifier
     */
    @SuppressLint("HardwareIds")
    fun getUUID(): String {

        if (QuickVariables.UUID == "")
            QuickVariables.UUID = Settings.Secure.getString(
                QuickInjectable.applicationContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )
        return QuickVariables.UUID
    }

    /**
     * checks the current application version name
     * @param directRequest used to either check directly the version name or get the value stored in the shared preference
     * @return the version name
     */
    fun getVersionName(directRequest: Boolean): String {

        if (directRequest) {
            return try {
                val version = QuickInjectable.applicationContext()
                    .packageManager.getPackageInfo(QuickInjectable.applicationContext().packageName, 0).versionName
                version.addWithId(QuickVariables.VERSION_NAME)
                version
            } catch (e: Exception) {
                ""
            }
        }
        return QuickInjectable.pref()
            .get(QuickVariables.VERSION_NAME)
    }

    /**
     * gets if the application is backgrounded by the user
     * @return if the application is in the background
     */
    fun backgrounded(): Boolean {

        var isInBackground = true
        var tasksList: List<*>? = null
        val activityManager = QuickInjectable.applicationContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        if (Build.VERSION.SDK_INT > 20) tasksList = activityManager.runningAppProcesses

        else {
            try {
                @Suppress("DEPRECATION")
                tasksList = activityManager.getRunningTasks(1)

            } catch (ignored: Exception) {
            }
        }

        if (tasksList != null && tasksList.isNotEmpty()) {

            when {

                Build.VERSION.SDK_INT > 22 -> {

                    for (processInfo in activityManager.runningAppProcesses) {

                        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

                            for (activeProcess in processInfo.pkgList) {

                                if (activeProcess == QuickInjectable.applicationContext().packageName) isInBackground = false
                            }
                        }
                    }

                    return isInBackground
                }

                Build.VERSION.SDK_INT > 20 -> return activityManager.runningAppProcesses[0].processName != QuickInjectable.applicationContext().packageName

                else -> @Suppress("DEPRECATION") return activityManager.getRunningTasks(1)[0].topActivity?.packageName != QuickInjectable.applicationContext().packageName
            }
        }

        else return false
    }

    /**
     * gets if network is available
     * @return if the device has 3g and wifi enabled
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(): Boolean {

        try {
            val isOnline = isOnline()

            if (isOnline) {

                if (QuickInjectable.pref().get(QuickInternetCheckService.ONLINE_SINCE_KEY) == "") QuickDateUtils.getCurrentDate(true).addWithId(
                    QuickInternetCheckService.ONLINE_SINCE_KEY
                )

                "".addWithId(QuickInternetCheckService.OFFLINE_SINCE_KEY)
            }

            else {

                if (QuickInjectable.pref().get(QuickInternetCheckService.OFFLINE_SINCE_KEY) == "") QuickDateUtils.getCurrentDate(true).addWithId(
                    QuickInternetCheckService.OFFLINE_SINCE_KEY
                )

                "".addWithId(QuickInternetCheckService.ONLINE_SINCE_KEY)
            }

            return isOnline

        } catch (e: Exception) {
            return false
        }
    }
}
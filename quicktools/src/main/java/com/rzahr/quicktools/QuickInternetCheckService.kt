package com.rzahr.quicktools

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rzahr.quicktools.extensions.addWithId
import com.rzahr.quicktools.utils.QuickAppUtils
import com.rzahr.quicktools.utils.QuickDateUtils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * @author Rashad Zahr
 *
 * service used to check for a valid internet connection by calling a simple url call every 15 seconds
 */
@Suppress("unused")
class QuickInternetCheckService : Service() {

    private var mOnlineTemp: Boolean = true

    // Binder given to clients
    private val mBinder = LocalBinder()

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {

        // Return this instance of LocalService so clients can call public methods
        fun getService(): QuickInternetCheckService = this@QuickInternetCheckService

        fun startCheck() {
            sTimer = Timer()

            val task = object : TimerTask() {
                @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                override fun run() {
                    setConnectionState(performChecks(sTimer!!, this@QuickInternetCheckService), this@QuickInternetCheckService)
                }
            }

            sTimer!!.scheduleAtFixedRate(task, 0, 15000) // service executes task every 15 seconds
        }
    }

    private fun setConnectionState(connected: Boolean, service: Service) {

        //if (mOnlineTemp != connected) {

        try {

            val intent = Intent(KEY)
            intent.putExtra(IS_ONLINE_KEY, connected)
            LocalBroadcastManager.getInstance(service).sendBroadcast(intent)
        }

        catch (e: Exception) {

            QuickLogWriter.errorLogging("Error updating connection icon", e.toString())
        }

        mOnlineTemp = connected
    }

    companion object {

        var sTimer: Timer? = null

        const val IS_ONLINE_KEY = "isOnline"
        const val ONLINE_SINCE_KEY = "onlineSince"
        const val OFFLINE_SINCE_KEY = "offlineSince"
        const val KEY = "com.rzahr.quicktools.CONNECTION_CHECKER_BROAD_CAST_IDENTIFIER"

        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        fun initQuickInternetCheckServiceConnection(mConnectionCheckerServiceConnection: ServiceConnection?): ServiceConnection? {

            if (mConnectionCheckerServiceConnection == null) {

                return initServiceConnection({ binder ->

                    binder.getService()

                }, {})
            }

            return mConnectionCheckerServiceConnection
        }

        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        fun bindToConnectionCheckerService(mConnectionCheckerServiceConnection: ServiceConnection?, mContext: Context): ServiceConnection? {

            val mConnectionCheckerServiceConnectionTemp = initQuickInternetCheckServiceConnection(mConnectionCheckerServiceConnection)

            // Bind to LocalService
            mConnectionCheckerServiceConnectionTemp?.let {Intent(mContext, QuickInternetCheckService::class.java).also { intent ->
                mContext.bindService(intent,it, Context.BIND_AUTO_CREATE)
            }}

            return mConnectionCheckerServiceConnectionTemp
        }

        fun unBindToConnectionCheckerService(mConnectionCheckerServiceConnection: ServiceConnection?, mActivity: Context){

            mConnectionCheckerServiceConnection?.let {mActivity.unbindService(it)}
        }

        /**
         * initialize the service connection object
         * @param onServiceConnected: on service connected function trigger
         * @param onServiceDisconnected: on service disconnected function trigger
         * @return service connection object
         */
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        fun initServiceConnection (onServiceConnected: (binder: LocalBinder) -> Unit, onServiceDisconnected: () -> Unit): ServiceConnection {

            return object : ServiceConnection {

                override fun onServiceConnected(className: ComponentName, service: IBinder) {

                    val binder = service as LocalBinder
                    binder.startCheck()
                    onServiceConnected(binder)
                }

                override fun onServiceDisconnected(arg0: ComponentName) {

                    onServiceDisconnected()
                }
            }
        }

        /**
         * @return the saved shared preference boolean value if the device is online
         */
        fun getIsOnline(): Boolean {

            return QuickInjectable.pref().getBoolean(IS_ONLINE_KEY)
        }

        /**
         * @return the saved shared preference value of the date when the device gained internet
         */
        fun getOnlineSince(): String {

            return QuickInjectable.pref().get(ONLINE_SINCE_KEY)
        }

        /**
         * @return the saved shared preference value of the date when the device became offline
         */
        fun getOfflineSince(): String {

            return QuickInjectable.pref().get(OFFLINE_SINCE_KEY)
        }
    }

    override fun onBind(intent: Intent): IBinder? {

        return mBinder
    }

    /**
     * cancel the timer task
     */
    override fun onDestroy() {
        super.onDestroy()

        if (sTimer != null) sTimer!!.cancel()
    }


    /**
     * cancel the periodical timer trigger and stop the service
     */
    private fun cancel(timer: Timer?, service: Service) {

        timer?.cancel()
        service.stopSelf()
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun performChecks(timer: Timer, service: Service): Boolean {

        //In case the app brought to background, then stop the service because no need
        if (QuickAppUtils.backgrounded()) cancel(timer, service)

        else {

            //In case the wifi and 3g are off then no need to check because there wont be any internet
            return if (!QuickAppUtils.isOnline()) {

                if (!QuickInjectable.pref().getBoolean(IS_ONLINE_KEY)) setOfflineSince(
                    QuickDateUtils.getCurrentDate(true), true)

                else setOfflineSince(QuickDateUtils.getCurrentDate(true), false)

                setOnlineSince(QuickDateUtils.getCurrentDate(true), false)

                false.addWithId(IS_ONLINE_KEY)

                false
            }

            else checkNetwork() //Check if internet is available
        }

        return true
    }

    /**
     * save in shared pref the time the user got offline in
     *
     * @param date               the date
     * @param checkIfNullOrEmpty the check if null or empty
     */
    private fun setOfflineSince(date: String, checkIfNullOrEmpty: Boolean) {

        if (checkIfNullOrEmpty) {

            if (QuickInjectable.pref().get(OFFLINE_SINCE_KEY).isEmpty()) date.addWithId(
                OFFLINE_SINCE_KEY)
        }

        else date.addWithId(OFFLINE_SINCE_KEY)
    }

    /**
     * save in shared pref the time the user got online in
     *
     * @param date               the date
     * @param checkIfNullOrEmpty the check if null or empty
     */
    private fun setOnlineSince(date: String, checkIfNullOrEmpty: Boolean) {

        if (checkIfNullOrEmpty) if (QuickInjectable.pref().get(ONLINE_SINCE_KEY) == "") date.addWithId(
            ONLINE_SINCE_KEY)

        else date.addWithId(ONLINE_SINCE_KEY)
    }

    /**
     * Set time since online or offline.
     *
    private fun setTimeSinceOnlineOrOffline(prefIdentifier: String) {
    val now = Date()
    try {
    //Case when the value is reset or first time
    if (QuickInjectable.pref().get(prefIdentifier) == "0") {
    val timeSinceOffline = now.time - TaskListAndOptionsActivity.sDateSinceLogin.time // The current time minus the last time the check was made

    timeSinceOffline.toString().addWithId(prefIdentifier) //Set the time
    TaskListAndOptionsActivity.sDateSinceLogin.time = now.time //Set the last check time as the current time.
    } else {
    val timeSinceOffline = java.lang.Long.parseLong(QuickInjectable.pref().get(prefIdentifier))
    val sum = now.time - TaskListAndOptionsActivity.sDateSinceLogin.time + timeSinceOffline
    TaskListAndOptionsActivity.sDateSinceLogin.time = now.time
    sum.toString().addWithId(prefIdentifier) //Set the time
    }
    } catch (e: Exception) {
    QuickLogWriter.errorLogging("Unable to set the time for offline and online", e)
    }
    }*/

    /**
     * Check network by calling in a url that will return minimal data (to decrease consumption). the check will be made on the status code returned
     */
    private fun checkNetwork(): Boolean {

        var responseCode = 0

        try {

            val httpURLConnection = URL("http://clients3.google.com/generate_204").openConnection() as HttpURLConnection
            httpURLConnection.setRequestProperty("User-Agent", "Android")
            httpURLConnection.setRequestProperty("Connection", "close")
            httpURLConnection.connectTimeout = 2000
            httpURLConnection.connect()
            responseCode = httpURLConnection.responseCode
        }
        catch (ignored: IOException) {
        }

        //Response code ok
        if (responseCode == 204) {

            if (QuickInjectable.pref().getBoolean(IS_ONLINE_KEY)) setOnlineSince(
                QuickDateUtils.getCurrentDate(true), true)

            else setOnlineSince(QuickDateUtils.getCurrentDate(true), false)

            setOfflineSince("", false)

            true.addWithId(IS_ONLINE_KEY)

            return true
        }

        else {

            if (!QuickInjectable.pref().getBoolean(IS_ONLINE_KEY)) setOfflineSince(
                QuickDateUtils.getCurrentDate(true), true)

            else setOfflineSince(QuickDateUtils.getCurrentDate(true), false)

            setOnlineSince("", false)

            false.addWithId(IS_ONLINE_KEY)

            return false
        }
    }
}
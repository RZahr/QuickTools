package com.rzahr.quicktools

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import javax.inject.Inject

@Suppress("unused")
class QuickNotificationUtils @Inject constructor(val context: Context) : ContextWrapper(context) {

    private var mManager: NotificationManager? = null

    /**
     * initializer function required for the class to function
     * @param channelId: the channel identifier
     * @param channelName: the channel name
     * @param channelDescription: the channel description
     * @param enableLight: if the notification causes the device to beam
     * @param enableVibration: if the notification causes the device to vibrate
     * @param lockScreenVisibility: the visibility defaulted to public
     * @param importance: the notification importance
     */
    fun initializer(channelId: String, channelName: String, channelDescription: String, enableLight: Boolean = true, enableVibration: Boolean = true, @SuppressLint(
        "InlinedApi"
    ) lockScreenVisibility: Int = Notification.VISIBILITY_PUBLIC, @SuppressLint("InlinedApi") importance: Int = NotificationManager.IMPORTANCE_DEFAULT, withSound: Boolean = false) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel(channelId, channelName, channelDescription, enableLight, enableVibration, lockScreenVisibility, importance, withSound)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String, channelName: String, channelDescription: String, enableLight: Boolean, enableVibration: Boolean,
                              lockScreenVisibility: Int, importance: Int, withSound: Boolean) {

        if (getManager()!!.getNotificationChannel(channelId) != null) {
            return
        }
        // create notification channel
        val notificationChannel = NotificationChannel(channelId, channelName, importance)

        notificationChannel.description = channelDescription
        notificationChannel.setShowBadge(false)
        notificationChannel.name = channelName
        notificationChannel.enableLights(enableLight)
        notificationChannel.enableVibration(enableVibration)
        notificationChannel.lightColor = Color.GRAY
        notificationChannel.lockscreenVisibility = lockScreenVisibility

        if (!withSound) notificationChannel.setSound(null, null)

        getManager()!!.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String, channelName: String, importance: Int) {

        if (getManager()!!.getNotificationChannel(channelId) != null) {
            return
        }
        // create notification channel
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        getManager()!!.createNotificationChannel(notificationChannel)
    }

    private fun getManager(): NotificationManager? {

        if (mManager == null) mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return mManager
    }

    fun getNotificationBuilder(title: String, body: String, onGoing: Boolean, smallIcon: Int, id: String, logo: Int?): NotificationCompat.Builder {
        if (logo != null) {
            val largeIcon = BitmapFactory.decodeResource(
                context.resources,
                logo
            )

            return NotificationCompat.Builder(context, id)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setBadgeIconType(smallIcon)
                .setContentTitle(title)
                .setContentText(body)
                .setOngoing(onGoing)
                .setAutoCancel(!onGoing)
                .setWhen(System.currentTimeMillis())
        }
        else return NotificationCompat.Builder(context, id)
            .setSmallIcon(smallIcon)
            .setBadgeIconType(smallIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setOngoing(onGoing)
            .setAutoCancel(!onGoing)
            .setWhen(System.currentTimeMillis())
    }
}
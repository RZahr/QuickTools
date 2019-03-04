package com.rzahr.quicktools

import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import javax.inject.Inject
import kotlin.random.Random


//val ALERT_CHANNEL_NAME = "ALERT CHANNEL"
const val NOTIFICATION_CHANNEL_NAME = "FT FFMS NOTIFICATION CHANNEL"
const val ALERT_CHANNEL_NAME = "FT FFMS ALERT CHANNEL"

class NotificationUtils @Inject constructor(val context: Context, val channelId: String, val channelName: String, val channelDescription: String, val enableLight: Boolean = true, val enableVibration: Boolean = true, val lockScreenVisibility: Int = Notification.VISIBILITY_PUBLIC, val importance: Int = NotificationManager.IMPORTANCE_DEFAULT) : ContextWrapper(context) {

    private var mManager: NotificationManager? = null

    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {

        if (getManager()!!.getNotificationChannel(channelId) != null) {
            return
        }
        // create notification channel
        val notificationChannel = NotificationChannel(channelId, NOTIFICATION_CHANNEL_NAME, importance)

        notificationChannel.description = channelDescription
        notificationChannel.setShowBadge(false)
        notificationChannel.name = channelName
        notificationChannel.enableLights(enableLight)
        notificationChannel.enableVibration(enableVibration)
        notificationChannel.lightColor = Color.GRAY
        notificationChannel.lockscreenVisibility = lockScreenVisibility
        notificationChannel.setSound(null, null)

        getManager()!!.createNotificationChannel(notificationChannel)
    }


    fun setSoundAndVibrate(builder: NotificationCompat.Builder) {

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) builder.priority = NotificationManager.IMPORTANCE_HIGH

        else  builder.priority = Notification.PRIORITY_HIGH

        builder.priority = NotificationCompat.PRIORITY_HIGH   // heads-up

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        builder.setSound(alarmSound)
        builder.setLights(Color.GREEN, 3000, 3000)
        builder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        builder.setDefaults(Notification.DEFAULT_ALL)
    }

    fun openTopActivityOnClick(builder: NotificationCompat.Builder, context: Context, currentActivity: Activity?, defaultActivity: Class<Any>?) {

        var resultIntent: Intent? = null

        currentActivity?.let { resultIntent= Intent(context, it::class.java) }

        if (currentActivity == null && defaultActivity!= null) resultIntent= Intent(context, defaultActivity)

        if (resultIntent != null) {

            resultIntent?.action = Intent.ACTION_MAIN
            resultIntent?.addCategory(Intent.CATEGORY_LAUNCHER)

            val pendingIntent = PendingIntent.getActivity(context, Random.nextInt(), resultIntent, 0)

            builder.setContentIntent(pendingIntent)
        }
    }

    private fun getManager(): NotificationManager? {
        if (mManager == null) mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        return mManager
    }

    fun getNotificationBuilder(title: String, body: String, onGoing: Boolean, smallIcon: Int, id: String, logo: Int): NotificationCompat.Builder {
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
}
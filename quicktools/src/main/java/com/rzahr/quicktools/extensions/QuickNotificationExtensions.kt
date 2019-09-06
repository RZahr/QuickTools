package com.rzahr.quicktools.extensions

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rzahr.quicktools.QuickInjectable
import kotlin.random.Random

/**
 * sets the notification to vibrate and ring
 */
fun NotificationCompat.Builder.setSoundAndVibrate() {

    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) this.priority = NotificationManager.IMPORTANCE_HIGH

    else  this.priority = Notification.PRIORITY_HIGH

    this.priority = NotificationCompat.PRIORITY_HIGH   // heads-up

    val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    this.setSound(alarmSound)
    this.setLights(Color.GREEN, 3000, 3000)
    this.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

    this.setDefaults(Notification.DEFAULT_ALL)
}

/**
 * opens activity on notification click
 * @param context the context
 * @param defaultActivity the default activity to open in case the current activity cannot be fetched
 */
fun NotificationCompat.Builder.openTopActivityOnClick(context: Context, defaultActivity: Class<Activity>?) {

    val currentActivity = QuickInjectable.currentActivity()

    var resultIntent: Intent? = null

    currentActivity?.let { resultIntent= Intent(context, it::class.java) }

    if (currentActivity == null && defaultActivity!= null) resultIntent= Intent(context, defaultActivity)

    if (resultIntent != null) {

        resultIntent?.action = Intent.ACTION_MAIN
        resultIntent?.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent = PendingIntent.getActivity(context, Random.nextInt(), resultIntent, 0)

        this.setContentIntent(pendingIntent)
    }
}
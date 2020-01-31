@file:Suppress("MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.text.Spanned
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence
import com.rzahr.quicktools.*
import com.rzahr.quicktools.extensions.*
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import kotlinx.android.synthetic.main.custom_three_option_alert.view.*

/**
 * @author Rashad Zahr
 *
 * object used as a helper to build common UI views
 */
object QuickUIUtils {

    /**
     * shows a bubble on view as a hint to what it does with the help of a third party library: com.elconfidencial.bubbleshowcase:bubbleshowcase
     * @param showCases: an array of @BubbleShowCaseBuilder which is created using addShowCase
     */
    fun showBubbles(showCases: Array<BubbleShowCaseBuilder>) {

        val bubbleSequence =  BubbleShowCaseSequence()

        for (showCase in showCases) bubbleSequence.addShowCase(showCase)

        bubbleSequence.show()
    }

    /**
     * used to animate a background view with colors
     * use example: QuickUIUtils.animateBackgroundWithColors(ValueAnimator.ofObject(ArgbEvaluator(), color1, color3, color4, color1), this)
     * @param colorAnimation: the color animation value animator
     * @param views: the views
     * @return a value animator
     */
    fun animateBackgroundWithColors(colorAnimation: ValueAnimator, vararg views: View): ValueAnimator {

        colorAnimation.duration = 10850 // milliseconds
        colorAnimation.addUpdateListener { animator -> for (view in views) view.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.start()
        colorAnimation.repeatCount = ValueAnimator.INFINITE

        return colorAnimation
    }

    /**
     * sends a push notification
     * @param title: the push notification title
     * @param message: the push notification message
     * @param utils: the notification utils class
     * @param context: the context
     * @param key: the notification key id
     * @param smallIcon: the notification small icon
     * @param id: the notification channel id
     * @param logo: the logo
     * @param defaultActivity: the default activity that needs to be opened
     */
    fun sendNotification(title: String, message: String, utils: QuickNotificationUtils, context: Context, key: String = title + message, smallIcon: Int,
                         id: String, logo: Int, defaultActivity: Class<Activity>?) {

        val notificationCompatBuilder = utils.getNotificationBuilder(title, message, false, smallIcon, id, logo)
        notificationCompatBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        notificationCompatBuilder.openTopActivityOnClick(context, defaultActivity)
        notificationCompatBuilder.setSoundAndVibrate()
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(key.hashCode(), notificationCompatBuilder.build())
    }

    /**
     * sets a badge number on drawable
     * @param icon: the icon layer drawable
     * @param count: the count
     * @param reuse: the drawable
     * @param backgroundColor: the backgroundColor color
     * @param textColor: the text color
     * @param textSize: the text size
     */
    fun setBadgeOnDrawable(icon: LayerDrawable, count: Int, backgroundColor: Int, reuse: Drawable?, textColor: Int = R.color.white, textSize: Float = 14f,
                           badgeDrawable: Int, setDrawableByLayerId: (icon: LayerDrawable, badge: Drawable) -> Unit) {

        val badge = if (reuse != null && reuse is QuickBadgeDrawable) {
            reuse.invalidateSelf()
            (reuse as QuickBadgeDrawable?)!!
        } else QuickBadgeDrawable(backgroundColor, textColor, textSize)

        if (count > 99) badge.setCount("99+")
        else badge.setCount(count.toString() + "")

        icon.mutate()
        icon.setDrawableByLayerId(badgeDrawable, badge)

        //setDrawableByLayerId(icon, badge)
    }

    @SuppressLint("InflateParams")
    private fun createCustomAlert(title: String, message: String, cancelable: Boolean, context: Context, center: Boolean = false, layout: Int = R.layout.custom_alert_dialog, withAnimation: Boolean = false): Array<Any?> {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        val dialogView = LayoutInflater.from(context).inflate(layout, null)
        if (withAnimation)dialogView?.rzBottomOfFloat(300)?.subscribe()
        builder.setView(dialogView)
        builder.setCancelable(false)

        if (layout == R.layout.custom_alert_dialog) {
            dialogView.alert_title_tv.text = title
            dialogView.alert_description_tv.text = message
        }

        else if (layout == R.layout.custom_three_option_alert) {
            dialogView.alert_text_title_tv.text = title
            dialogView.alert_text_description_tv.text = message
        }

        var alert: AlertDialog? = null

        if(!(context as Activity).isFinishing) alert = builder.show()
//        if(!(context as Activity).isFinishing) alert = builder.create()

        // Let's start with animation work. We just need to create a style and use it here as follow.
//        if (alert?.window != null) alert.window?.attributes?.windowAnimations = R.style.SlidingDialogAnimation

//        if(!context.isFinishing) alert = builder.show()

        alert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (cancelable) {

            builder.setOnKeyListener { dialog, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                }
                true
            }
        }

        else {

            builder.setCancelable(false)

            builder.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                }
                true
            }
        }

        if (center) {

            val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            p.weight = 2f
            dialogView.admin_ok_mb.layoutParams = p
        }

        return arrayOf(builder, dialogView, alert)
    }

    @SuppressLint("InflateParams")
    private fun createCustomAlert(title: String, message: Spanned, cancelable: Boolean, context: Context, center: Boolean = false, withAnimation: Boolean = false): Array<Any?> {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)

       if (withAnimation) dialogView?.rzBottomOfFloat(300)
            //  ?.mergeWith(dialogView?.rzVibrate(100))
            ?.subscribe()
        builder.setView(dialogView)
        builder.setCancelable(false)

        dialogView.alert_title_tv.text = title
        dialogView.alert_description_tv.text = message

        var alert: AlertDialog? = null

        if(!(context as Activity).isFinishing) alert = builder.show()
//        if(!(context as Activity).isFinishing) alert = builder.create()

        // Let's start with animation work. We just need to create a style and use it here as follow.
//        if (alert?.window != null) alert.window?.attributes?.windowAnimations = R.style.SlidingDialogAnimation

//        if(!context.isFinishing) alert = builder.show()

        alert?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (cancelable) {

            builder.setOnKeyListener { dialog, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                }
                true
            }
        }

        else {

            builder.setCancelable(false)

            builder.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                }
                true
            }
        }

        if (center) {

            val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            p.weight = 2f
            dialogView.admin_ok_mb.layoutParams = p
        }

        return arrayOf(builder, dialogView, alert)
    }

    /**
     * creates a quick custom alert dialog
     * @param title: the alert title
     * @param message: the alert message
     * @param negativeButtonText: the alert negative button text
     * @param positiveButtonText: the alert positive button text
     * @param context: the context
     * @param positiveAction: the action after positive button clicked
     * @param negativeAction: the action after negative button clicked
     * @param hasNegativeButton: boolean value representing if the negative button is available
     * @param cancelable: boolean value representing if the alert dialog is cancellable
     * @param logo: the optional logo icon
     */
    fun createQuickAlert(title: String, message: String, negativeButtonText: String, positiveButtonText: String, context: Context, positiveAction: () -> Unit, negativeAction: () -> Unit,
                         hasNegativeButton: Boolean = true, cancelable: Boolean = true, logo: Drawable? = null, centerButton: Boolean = false, withAnimation: Boolean = false): AlertDialog? {

        val a = createCustomAlert(title, message, cancelable, context, centerButton, withAnimation)
        // create the alert dialog and set it to cancellable or not depending on what was supplied
        val dialogView = a[1] as View
        val alert = a[2] as AlertDialog?

        // if the alert has a negative button then set it
        if (hasNegativeButton) {

            var negativeButtonTextTemp = negativeButtonText

            // if no negative text was provided and the alert has a negative button, then set it to the default close text
            if (negativeButtonTextTemp.isEmpty()) negativeButtonTextTemp = context.getString(R.string.close)

            if (positiveButtonText.isEmpty())  {

                dialogView.admin_cancel_mb.rzSetVisibilityInvisible()
                dialogView.admin_ok_mb.rzSetVisible()

                dialogView.admin_ok_mb.text = negativeButtonTextTemp

                // set the negative button action
                dialogView.admin_ok_mb.setOnClickListener {

                    //  run {
                    alert?.cancel()
                    negativeAction()
                    //   }
                }
            }

            else {

                dialogView.admin_cancel_mb.rzSetVisible()
                dialogView.admin_cancel_mb.text = negativeButtonTextTemp

                // set the negative button action
                dialogView.admin_cancel_mb.setOnClickListener {

                    //  run {
                    alert?.cancel()
                    negativeAction()
                    //   }
                }
            }
        }

        if (logo != null) {

            dialogView.alert_iv.rzSetVisible()
            dialogView.alert_iv.setImageDrawable(logo)
        }

        // if the alert has a positive button (the positive button text is not empty) then set the positive button action to the action supplied
        if (positiveButtonText.isNotEmpty()) {

            dialogView.admin_ok_mb.rzSetVisible()
            dialogView.admin_ok_mb.text = positiveButtonText

            // set the negative button action
            dialogView.admin_ok_mb.setOnClickListener {

                positiveAction()

                alert?.cancel()
            }
        }
        // finally, show the alert button
        //showAlert(builder)

        return alert
    }

    /**
     * creates a quick custom alert dialog
     * @param title: the alert title
     * @param message: the alert message
     * @param negativeButtonText: the alert negative button text
     * @param positiveButtonText: the alert positive button text
     * @param context: the context
     * @param positiveAction: the action after positive button clicked
     * @param negativeAction: the action after negative button clicked
     * @param cancelable: boolean value representing if the alert dialog is cancellable
     * @param logo: the optional logo icon
     */
    fun createThreeOptionedAlert(title: String, message: String, negativeButtonText: String, positiveButtonText: String, thirdButtonText: String, context: Context, positiveAction: () -> Unit, negativeAction: () -> Unit, thirdAction: () -> Unit,
                                 cancelable: Boolean = true, logo: Drawable? = null, withAnimation: Boolean = false): AlertDialog? {

        val a = createCustomAlert(title, message, cancelable, context, false, R.layout.custom_three_option_alert, withAnimation)
        // create the alert dialog and set it to cancellable or not depending on what was supplied
        val dialogView = a[1] as View
        val alert = a[2] as AlertDialog?

        dialogView.option_one_mb.text = negativeButtonText

        // set the negative button action
        dialogView.option_one_mb.setOnClickListener {

            //  run {
            alert?.cancel()
            negativeAction()
            //   }
        }

        if (logo != null) {

            dialogView.alert_image_iv.rzSetVisible()
            dialogView.alert_image_iv.setImageDrawable(logo)
        }

        // if the alert has a positive button (the positive button text is not empty) then set the positive button action to the action supplied
        dialogView.option_two_mb.text = positiveButtonText

        dialogView.option_three_mb.text = thirdButtonText

        // set the negative button action
        dialogView.option_two_mb.setOnClickListener {

            positiveAction()

            alert?.cancel()
        }

        dialogView.option_three_mb.setOnClickListener {

            thirdAction()

            alert?.cancel()
        }

        return alert
    }

    /**
     * creates a quick custom alert dialog
     * @param title: the alert title
     * @param message: the alert message
     * @param negativeButtonText: the alert negative button text
     * @param positiveButtonText: the alert positive button text
     * @param context: the context
     * @param positiveAction: the action after positive button clicked
     * @param negativeAction: the action after negative button clicked
     * @param hasNegativeButton: boolean value representing if the negative button is available
     * @param cancelable: boolean value representing if the alert dialog is cancellable
     * @param logo: the optional logo icon
     */
    fun createQuickAlert(title: String, message: Spanned, negativeButtonText: String, positiveButtonText: String, context: Context, positiveAction: () -> Unit, negativeAction: () -> Unit,
                         hasNegativeButton: Boolean = true, cancelable: Boolean = true, logo: Drawable? = null, centerButton: Boolean = false, withAnimation: Boolean = false): AlertDialog? {

        val a = createCustomAlert(title, message, cancelable, context, centerButton, withAnimation)
        val dialogView = a[1] as View
        val alert = a[2] as AlertDialog?

        // if the alert has a negative button then set it
        if (hasNegativeButton) {

            var negativeButtonTextTemp = negativeButtonText

            // if no negative text was provided and the alert has a negative button, then set it to the default close text
            if (negativeButtonTextTemp.isEmpty()) negativeButtonTextTemp = context.getString(R.string.close)

            if (positiveButtonText.isEmpty())  {

                dialogView.admin_cancel_mb.rzSetVisibilityInvisible()
                dialogView.admin_ok_mb.rzSetVisible()

                dialogView.admin_ok_mb.text = negativeButtonTextTemp

                // set the negative button action
                dialogView.admin_ok_mb.setOnClickListener {

                    //  run {
                    alert?.cancel()
                    negativeAction()
                    //   }
                }
            }

            else {

                dialogView.admin_cancel_mb.rzSetVisible()

                dialogView.admin_cancel_mb.text = negativeButtonTextTemp

                // set the negative button action
                dialogView.admin_cancel_mb.setOnClickListener {

                    //  run {
                    alert?.cancel()
                    negativeAction()
                    //   }
                }
            }
        }


        if (logo != null) {

            dialogView.alert_iv.rzSetVisible()
            dialogView.alert_iv.setImageDrawable(logo)
        }

        // if the alert has a positive button (the positive button text is not empty) then set the positive button action to the action supplied
        if (positiveButtonText.isNotEmpty()) {

            dialogView.admin_ok_mb.rzSetVisible()
            dialogView.admin_ok_mb.text = positiveButtonText

            // set the negative button action
            dialogView.admin_ok_mb.setOnClickListener {

                positiveAction()

                alert?.cancel()
            }
        }

        return alert
    }
}
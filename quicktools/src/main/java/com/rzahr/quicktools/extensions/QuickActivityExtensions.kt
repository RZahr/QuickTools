@file:Suppress("unused")

package com.rzahr.quicktools.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * @author Rashad Zahr
 *
 * used to set the activity to full screen
 */
fun Activity.setFullScreen() {

    // set to full screen
    this.requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * creates the activity toolbar
 *
 * @param title the toolbar title
 * @param subTitle the toolbar subtitle
 * @param withNavigation boolean value specifying if the activity supports back
 * @param withLogo boolean value specifying if the activity has a logo
 */
fun AppCompatActivity.createToolbar(title: String, subTitle: String, withNavigation: Boolean?, withLogo: Boolean?, backgroundColorId: Int? = null, toolbarId: Int, titleId: Int, subtitleId: Int, logoImage: Int, icon: Int? =null) {

    val toolbar = this.findViewById(toolbarId) as Toolbar
    this.setSupportActionBar(toolbar)

    backgroundColorId?.let { toolbar.setBackgroundResource(it) }

    if (title.isNotEmpty()) {

        val titleTextView = toolbar.findViewById(titleId) as TextView
        titleTextView.text = title
        titleTextView.visibility = View.VISIBLE
    }

    if (subTitle.isNotEmpty()) {

        val subTitleTextView = toolbar.findViewById(subtitleId) as TextView
        subTitleTextView.text = subTitle
        subTitleTextView.visibility = View.VISIBLE
    }

    if (withNavigation!! && this.supportActionBar != null) this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    if (withLogo!!) {

        val imageLogo = toolbar.findViewById(logoImage) as ImageView

        if (this.supportActionBar != null) this.supportActionBar!!.setDisplayShowTitleEnabled(false)

        imageLogo.visibility = View.VISIBLE

        icon?.let { imageLogo.setBackgroundResource(it) }
    }
}

/**
 * tool used to hide the keyboard from an activity
 */
fun Activity.hideKeyboard() {

    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

/**
 * lock the activity orientation in landscape or portrait
 * @param currentOrientation: the current orientation mode that will be locked in
 */
@SuppressLint("SourceLockedOrientationActivity")
fun Activity.lockOrientation(currentOrientation: Int = this.resources.configuration.orientation) {

    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

    else this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
}

/**
 * unlock the activity locked orientation
 */
fun Activity.unLockOrientation() {

    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

/**
 * show the hidden toolbar in the activity
 */
fun Activity?.showToolbar() {

    (this as AppCompatActivity?)?.supportActionBar!!.show()
}
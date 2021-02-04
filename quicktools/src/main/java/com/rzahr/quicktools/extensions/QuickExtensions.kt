@file:Suppress("unused")

package com.rzahr.quicktools.extensions

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.FileProvider
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickLogWriter
import com.rzahr.quicktools.R
import java.io.File
import java.util.*

/**
 * @author Rashad Zahr
 */

/**
 * saves instance state of a string
 * @param id: the id pointing to this string value
 * @param outState: the bundle where the value is saved in
 */
fun String.saveStateOf(id: String, outState: Bundle?) {

    outState?.putString(id, this)
}

/**
 * saves instance state of a boolean
 * @param id: the id pointing to this boolean value
 * @param outState: the bundle where the value is saved in
 */
fun Boolean.saveStateOf(id: String, outState: Bundle?) {

    outState?.putBoolean(id, this)
}

/**
 * saves instance state of a parcelable array list
 * @param id: the id pointing to this array list value
 * @param outState: the bundle where the value is saved in
 */
fun ArrayList<out Parcelable>.saveStateOf(id: String, outState: Bundle?) {

    outState?.putParcelableArrayList(id, this)
}

/**
 * saves instance state of a parcelable
 * @param id: the id pointing to this parcelable value
 * @param outState: the bundle where the value is saved in
 */
fun Parcelable.saveStateOf(id: String, outState: Bundle?) {

    outState?.putParcelable(id, this)
}

/**
 * gets the saved instance state of a parcelable array list
 * @param id: the id pointing to this object value
 */
fun <T : Parcelable> Bundle?.getStateOf(id: String): ArrayList<T>? {

    return this?.getParcelableArrayList(id)
}

/**
 * @return the file URI
 */
fun File.getFileURI(): Uri {

    return if (Build.VERSION.SDK_INT >= 24) FileProvider.getUriForFile(QuickInjectable.applicationContext2(), QuickInjectable.applicationContext2().applicationContext.packageName + ".provider", this) else Uri.fromFile(this)
}

/**
 * converts drawable to bitmap
 * @return the bitmap
 */
fun Drawable.toBitmap(): Bitmap {

    if (this is BitmapDrawable) return this.bitmap

    var width = this.intrinsicWidth
    width = if (width > 0) width else 96

    var height = this.intrinsicHeight
    height = if (height > 0) height else 96

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

/**
 * opens a file if applicable
 * @param attachmentName: the attachment name
 * @param context: the context
 * @param activity: the activity
 * @param mimeType: the mime type
 */
fun File.openAttachment(attachmentName: String, context: Context, activity: Activity, mimeType: String) {

    if (attachmentName.isNotEmpty() && attachmentName != "-") {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val extension =
            android.webkit.MimeTypeMap.getFileExtensionFromUrl(this.getFileURI().toString())
        if (extension.equals("", ignoreCase = true))
            intent.setDataAndType(this.getFileURI(), "text/*")
        else
            intent.setDataAndType(this.getFileURI(), mimeType.toLowerCase(Locale.ENGLISH))

        activity.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_an_app)))
    }
}

/**
 * gets the saved instance state of a parcelable
 * the parcelable attribute is needed to differentiate between this method and the one for the array list of parcelable
 * @param id: the id pointing to this object value
 * @param parcelable: unused parameter
 * @return parcelable T
 */
fun <T : Parcelable> Bundle?.getStateOf(id: String, @Suppress("UNUSED_PARAMETER") parcelable: Int): T? {

    return this?.getParcelable(id)
}

/**
 * gets the saved instance state of a string
 * @param id: the id pointing to this string value
 * @param default: the default value of the string if it does not exist
 * @return a string value
 */
fun Bundle?.getStateOf(id: String, default: String = ""): String {

    return try {

        this?.getString(id, default)!!
    }

    catch (e:Exception){

        default
    }
}

/**
 * gets the saved instance state of a boolean
 * @param id: the id pointing to this boolean value
 * @param default: the default value of the boolean if it does not exist
 * @return a boolean value
 */
fun Bundle?.getStateOf(id: String, default: Boolean = false): Boolean {

    return try {

        this?.getBoolean(id, default)!!
    }

    catch (e:Exception){

        default
    }
}

/**
 * removes duplicates from an array list
 * @return a duplicate free array list
 */
fun ArrayList<String>.removeDuplicates(): Set<String> {

    val set = HashSet<String>()
    for (i in 0 until this.size) set.add(this[i])
    return set
}


/**
 * saves integer in shared preference
 * @param id: the id pointing to this integer value
 */
fun Int.addWithId(id: String) {

    QuickInjectable.pref().setIntValue(id, this)
}

/**
 * saves string in shared preference
 * @param id: the id pointing to this string value
 */
fun String.addWithId(id: String) {

    QuickInjectable.pref().setString(id, this)
}

/**
 * saves double in shared preference
 * @param id: the id pointing to this double value
 */
fun Double.addWithId(id: String) {

    QuickInjectable.pref().setString(id, this.toString())
}

/**
 * saves long in shared preference
 * @param id: the id pointing to this long value
 */
fun Long.addWithId(id: String) {

    QuickInjectable.pref().setLong(id, this)
}

/**
 * saves boolean in shared preference
 * @param id: the id pointing to this boolean value
 */
fun Boolean.addWithId(id: String) {

    QuickInjectable.pref().setBoolean(id, this)
}

/**
 * saves string in shared preference as default
 * @param id: the id pointing to this string value
 */
fun String.addAsDefaultWithId(id: String) {

    QuickInjectable.pref().setStringDefault(id, this)
}

/**
 * gets string from strings.xml
 * @param context: the context used to get the string value. in case the value is not passed then the application context is provided
 * @return a string value from the resources
 */
fun Int.get(context: Context = QuickInjectable.applicationContext()): String {

    return context.getString(this)
}

/**
 * saves long in shared preference as default
 * @param id: the id pointing to this string value
 */
fun Long.addAsDefaultWithId(id: String) {

    QuickInjectable.pref().setLongDefault(id, this)
}


/**
 * get shared Long value.
 * @return the Long
 */
fun String.getLongPrefValue(): Long {
    return QuickInjectable.pref().getLong(this)
}

/**
 * get shared boolean value boolean.
 * @return the boolean
 */
fun String.getBoolPrefValue(): Boolean {
    return QuickInjectable.pref().getBoolean(this)
}

/**
 * get shared String value String.
 * @return the String
 */
fun String.getStringPrefValue(): String {
    return QuickInjectable.pref().get(this)
}

/**
 * get shared Int value Int.
 * @return the Int
 */
fun String.getIntPrefValue(): Int {
    return QuickInjectable.pref().getInt(this)
}

/**
 * @return the preference value of the key passed
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified I>String.rzPrefVal(): I {

    return when (I::class.java.simpleName.toLowerCase(Locale.ENGLISH)) {

        "string" -> QuickInjectable.pref().get(this) as I
        "int" -> QuickInjectable.pref().getInt(this) as I
        "integer" -> QuickInjectable.pref().getInt(this) as I
        "boolean" -> QuickInjectable.pref().getBoolean(this) as I
        "bool" -> QuickInjectable.pref().getBoolean(this) as I
        "long" -> QuickInjectable.pref().getLong(this) as I
        else -> QuickInjectable.pref().get(this) as I
    }
}






/**
 * @param context: the context
 * @return if service class is running or not
 */
fun Class<*>.isMyServiceRunning(context: Context): Boolean {
    try {
        @Suppress("DEPRECATION")
        for (service in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
            Integer.MAX_VALUE
        ))
            if (this.name.toLowerCase(Locale.ENGLISH).contains(
                    service.service.className.toLowerCase(
                        Locale.ENGLISH
                    )
                )
            ) return true
    } catch (e: Exception) {
        QuickLogWriter.errorLogging("Error",e.toString())
    }

    return false
}

/**
 * starts the specified service
 */
fun Class<*>.startService(context: Context) {

    context.startService(Intent(context, this))
}

/**
 * stops the specified service
 */
fun Class<*>.stopService(context: Context) {

    context.stopService(Intent(context, this))
}

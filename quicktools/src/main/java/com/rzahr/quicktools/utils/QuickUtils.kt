@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Html
import android.text.Spanned
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickLogWriter
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.*
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * @author Rashad Zahr
 *
 * object used as a helper
 */
object QuickUtils {

    /**
     * remove illegal sql characters
     * @param value the value
     * @return pure value
     */
    fun removeIllegalSQLChars(value: String): String {

        @Suppress("NAME_SHADOWING") var value = value
        value = value.replace("'", " ")
        value = value.replace("&", "")
        value = value.replace("#", "")
        value = value.replace("\"", "")
        value = value.replace("^", "")

        return value
    }

    /**
     * vibrates the device
     * @param milliseconds: the duration of the vibration
     * @param amplitude: the amplitude
     */
    fun vibrateDevice(milliseconds: Long = 200, @SuppressLint("InlinedApi") amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {

        val vibrator = QuickInjectable.applicationContext2().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, amplitude))

        else vibrator.vibrate(milliseconds)
    }

    /**
     * rounds a number
     * @param value the value that will be rounded
     * @param places the number of places after the decimal
     * @return the rounded number
     */
    fun roundNumber(value: Double, places: Int): Double {

        @Suppress("NAME_SHADOWING") var value = value
        if (places < 0) throw IllegalArgumentException()
        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value *= factor

        return Math.round(value).toDouble() / factor
    }

    /**
     * uses rx java to perform a background task
     */
    fun backgroundUpdater(backgroundFunction: () -> Any?, ForegroundFunction: (it: Any?) -> Unit = {}, ErrorFunction: (it: Throwable) -> Unit? = {}, ErrorFunction2: () -> Any? = {}): Disposable {

        return Single.fromCallable { backgroundFunction() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess { ForegroundFunction(it) }
            .doOnError{ ErrorFunction(it) }
            .doOnError{ ErrorFunction2() }
            .subscribe({

            }, {
                ErrorFunction(it)
            })
    }

    /**
     * uses rx java to perform a background task
     */
    class BackgroundUpdaterN<T> constructor (backgroundFunction: () -> T?, foregroundFunction: (it: T?) -> Unit, errorFunction: (it: Throwable) -> Unit?, errorFunction2: () -> Any?, subscribeOn: Scheduler = Schedulers.io(), observeOn: Scheduler = AndroidSchedulers.mainThread()) {

        private val mDisposable: Disposable = Single.fromCallable { backgroundFunction() }
            .subscribeOn(subscribeOn)
            .observeOn(observeOn)
            .doAfterSuccess { foregroundFunction(it) }
            .doOnError { errorFunction(it) }
            .doOnError{ errorFunction2() }
            .subscribe({

            }, {
                errorFunction(it)
            })

        fun getDisposable() = mDisposable
    }

    /**
     * cancels the notification with the notification id passed
     * @param notificationId: the notification id that requires cancelling
     */
    fun cancelPendingNotifications(notificationId: String) {

        try {

            (QuickInjectable.applicationContext2().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(notificationId.hashCode())
        }

        catch (e:Exception) {

            QuickLogWriter.errorLogging("Error", e.toString())
        }
    }

    private fun canHandleIntent(intent: Intent, activity: Activity): Boolean {

        return activity.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }

    /**
     * opens google map application if available
     * @param uri: the uri of the application
     * @param activity: the activity requesting
     */
    fun openGoogleMapsApp(uri: String, activity: Activity) {

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")

        if (canHandleIntent(intent, activity))  activity.startActivity(intent)

        else Toast.makeText(activity.applicationContext, "Please Install Google Navigation", Toast.LENGTH_LONG).show()
    }

    /**
     * regular expression search
     * @param patternString: the regular expression pattern
     * @param word: the word that will be searched in
     * @return a string value of what was found
     */
    fun regEx(patternString: String, word: String): String {

        val matcher =  Pattern.compile(patternString).matcher(word)
        var matchedString = ""

        while (matcher.find()) matchedString = matcher.group()

        return matchedString
    }

    /**
     * string to html
     * @param html: the html structure string
     */
    fun fromHtml(html: String): Spanned {

        @Suppress("DEPRECATION")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY) else Html.fromHtml(html)
    }

    /**
     * request a certain permission
     * @param currentActivity: the current activity
     * @param manifestPermission: the manifest permission
     * @param permissionIdentifier: the permission identifier
     */
    fun requestPermission(currentActivity: Activity, manifestPermission: String, permissionIdentifier: Int) {

        ActivityCompat.requestPermissions(
            currentActivity,
            arrayOf(manifestPermission),
            permissionIdentifier
        )
    }

    /**
     * parse an input source to a document object
     * @param inputSource: the input source
     * @return a document object
     */
    @Throws(ParserConfigurationException::class, IOException::class, SAXException::class)
    fun parseDoc(inputSource: InputSource): Document {

        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        documentBuilderFactory.isNamespaceAware = true

        return documentBuilderFactory.newDocumentBuilder().parse(inputSource)
    }

    /**
     * converts an input string to a string value
     * @param inputStream: the input stream
     * @param newLineSupport: if new line is supported
     * @return a string from the input stream
     */
    fun convertStreamToString(inputStream: InputStream, newLineSupport: Boolean): String {

        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = null
        try {
            while ({ line = reader.readLine(); line }() != null) {
                if (newLineSupport) {
                    stringBuilder.append(line).append('\n')
                } else {
                    stringBuilder.append(line)
                }
            }
        } catch (e: IOException) {
            // printStackTrace(e)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                // printStackTrace(e)
            }
        }

        return stringBuilder.toString()
    }

    /**
     * safe close buffered writer.
     * @param bufferedWriter the buffered writer
     */
    fun safeCloseBufferedWriter(bufferedWriter: BufferedWriter?) {

        if (bufferedWriter != null) {

            bufferedWriter.flush()
            bufferedWriter.close()
        }
    }
}
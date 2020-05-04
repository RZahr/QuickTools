@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools

import android.os.Environment
import android.util.Log
import com.rzahr.quicktools.utils.QuickDateUtils
import com.rzahr.quicktools.utils.QuickUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object QuickLogWriter {

    const val TAG = "QuickTools_LogWriter"

    /**
     * get caller class string [ ].
     * @param level the level
     * @return the string [ ]
     */
    fun getCallerClass(level: Int): Array<String> {
        return try {
            val stElements = Thread.currentThread().stackTrace
            arrayOf(
                stElements[level + 1].lineNumber.toString() + "",
                stElements[level + 1].fileName,
                stElements[level + 1].methodName
            )
        } catch (e: Exception) {
            arrayOf("", "", "")
        }
    }
    @Suppress("DEPRECATION")
    @Deprecated("Uses Environment.getExternalStorageDirectory().toString() as the file path which is deprecated")
    fun logErrorHelper(callingMethod: Array<String>, msg: String, logFileNameTemp: String, error: String, folderName: String, deleteFileIfExist: Boolean) {

        Log.e(callingMethod[1] + " (" + callingMethod[0] + ")", "Func: " + callingMethod[2] + " Msg: " + msg + " //**//Error: " + error)
        appendContents(QuickInjectable.pref().get(logFileNameTemp) + ".txt", callingMethod[1] + "         Func: " + callingMethod[2] + " Line No. " + callingMethod[0] + " Msg: " + msg + " //**//Error: " + error + " \n",true, folderName, deleteFileIfExist)
    }
    @Suppress("DEPRECATION")
    @Deprecated("Uses Environment.getExternalStorageDirectory().toString() as the file path which is deprecated")
    fun logHelper(callingMethod: Array<String>, msg: String, logFileNameTemp: String, error: String, folderName: String, deleteFileIfExist: Boolean) {

        Log.w(callingMethod[1] + " (" + callingMethod[0] + ")", "Func: " + callingMethod[2] + " Msg: " + msg)
        appendContents("$logFileNameTemp.txt", callingMethod[1] + "         Func: " + callingMethod[2] + " Line No. " + callingMethod[0] + " Msg: " + msg +error+ " \n",true, folderName, deleteFileIfExist)
    }


    fun logErrorHelper(callingMethod: Array<String>, msg: String, filePath: String, error: String, deleteFileIfExist: Boolean) {

        Log.e(callingMethod[1] + " (" + callingMethod[0] + ")", "Func: " + callingMethod[2] + " Msg: " + msg + " //**//Error: " + error)
        appendContents(filePath, callingMethod[1] + "         Func: " + callingMethod[2] + " Line No. " + callingMethod[0] + " Msg: " + msg + " //**//Error: " + error + " \n",true, deleteFileIfExist)
    }

    fun logHelper(callingMethod: Array<String>, msg: String, filePath: String, error: String, deleteFileIfExist: Boolean) {

        Log.w(callingMethod[1] + " (" + callingMethod[0] + ")", "Func: " + callingMethod[2] + " Msg: " + msg)
        appendContents(filePath, callingMethod[1] + "         Func: " + callingMethod[2] + " Line No. " + callingMethod[0] + " Msg: " + msg +error+ " \n",true, deleteFileIfExist)
    }

    /**
     * Print stack trace.
     * @param e the e
     */
    fun printStackTrace(e: Exception) {
        e.printStackTrace()
    }

    fun debugLogging(message: Any) {

        var level = 4
        try {
            for (stack in Thread.currentThread().stackTrace) {

                if (stack.className.contains(QuickInjectable.applicationContext().packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true)) {

                    level = Thread.currentThread().stackTrace.indexOf(stack)
                    break
                }
            }
        }
        catch (ignored: java.lang.Exception){}

        val callingMethod = getCallerClass(level)
        Log.w(callingMethod[1] + " (" + callingMethod[0] + ")", "Func: " + callingMethod[2] + " Msg: " + message)
    }

    @Synchronized
    @Suppress("DEPRECATION")
    @Deprecated("Uses Environment.getExternalStorageDirectory().toString() as the file path which is deprecated")
    fun appendContents(sFileName: String, sContent: String, includeDate: Boolean, folderName: String, deleteFileIfExist: Boolean) {

        try {
            val filePath = Environment.getExternalStorageDirectory().toString() + "/" + folderName + "/" + sFileName
            val oFile = File(filePath)

            if (deleteFileIfExist && oFile.exists()) oFile.delete()

            if (!oFile.exists()) oFile.createNewFile()

            if (oFile.canWrite()) {
                val oWriter = BufferedWriter(FileWriter(File(filePath), true))
                try {
                    oWriter.newLine()
                    if (includeDate) oWriter.write(" ###" + QuickDateUtils.getCurrentDate(true, QuickDateUtils.SLASHED_FORMAT) + ":" + sContent + " \n\r")
                    else oWriter.write(sContent)
                } finally {
                    QuickUtils.safeCloseBufferedWriter(oWriter)
                }
            }
        } catch (oException: IOException) {
            Log.e(TAG, "Error in appendContents oException $oException")
        }
    }

    @Synchronized
    fun appendContents(filePath: String, sContent: String, includeDate: Boolean, deleteFileIfExist: Boolean) {

        try {
            val oFile = File(filePath)

            if (deleteFileIfExist && oFile.exists()) oFile.delete()

            if (!oFile.exists()) oFile.createNewFile()

            if (oFile.canWrite()) {
                val oWriter = BufferedWriter(FileWriter(File(filePath), true))
                try {
                    oWriter.newLine()
                    if (includeDate) oWriter.write(" ###" + QuickDateUtils.getCurrentDate(true, QuickDateUtils.SLASHED_FORMAT) + ":" + sContent + " \n\r")
                    else oWriter.write(sContent)
                } finally {
                    QuickUtils.safeCloseBufferedWriter(oWriter)
                }
            }
        } catch (oException: IOException) {
            Log.e(TAG, "Error in appendContents oException $oException")
        }
    }

    /**
     * Error logging.
     *
     * @param message   the msg
     * @param error the error
     */
    fun errorLogging(message: Any, error: Any) {
        try {

            var level = 4
            try {
                for (stack in Thread.currentThread().stackTrace) {

                    if (stack.className.contains(QuickInjectable.applicationContext().packageName, ignoreCase = true) && !stack.className.contains(this.javaClass.name, ignoreCase = true)) {

                        level = Thread.currentThread().stackTrace.indexOf(stack)
                        break
                    }
                }
            }
            catch (ignored: java.lang.Exception){}

            val callingMethod = getCallerClass(level)
            Log.e(
                callingMethod[1] + " (" + callingMethod[0] + ")",
                "Func: " + callingMethod[2] + " Msg: " + message + " //**//Error: " + error
            )
        }
        catch (ignored: java.lang.Exception){}
    }
}
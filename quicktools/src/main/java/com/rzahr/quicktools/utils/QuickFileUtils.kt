@file:Suppress("MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.webkit.MimeTypeMap
import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickLogWriter
import com.rzahr.quicktools.R
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * @author Rashad Zahr
 *
 * object used as a helper for file-related functions
 */
@Suppress("unused")
object QuickFileUtils {

    /**
     * gets the file mip-map type
     * @return the file mipmap
     */
    fun getFileMipMap(fileURi: Uri): String {

        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fileURi.toString()))

        if (mimeType == null) {

            val mimRegex = QuickUtils.regEx("[.].*", fileURi.toString())

            return if (mimRegex.isNotEmpty()) "application/$mimRegex" else ""
        }

        return mimeType.toLowerCase()
    }

    /**
     * delete a file
     * @param path: the path of the file to be deleted
     */
    fun deleteFile(path: String) {

        val fileToDelete = File(path)

        if (fileToDelete.exists()) fileToDelete.delete()
    }

    /**
     * delete a complete directory
     * @param: the path of the directory to be deleted
     */
    fun deleteDirectory(path: String) {

        val directoryToDelete = File(path)

        if (directoryToDelete.exists() && directoryToDelete.isDirectory) {

            if (directoryToDelete.list().isEmpty()) directoryToDelete.delete()

            else {

                for (file in directoryToDelete.list()) deleteDirectory(directoryToDelete.path + "/" + file)

                if (directoryToDelete.list().isEmpty()) directoryToDelete.delete()
            }
        }

        else if (directoryToDelete.exists()) directoryToDelete.delete()
    }

    /**
     * Create directory.
     * @param path        the path
     * @param withNoMedia the with no media
     * @return the state if the directory was created or not
     */
    fun createDirectory(path: String, withNoMedia: Boolean): String {

        val folder = File(path)
        var success = true
        if (!folder.exists()) success = folder.mkdir()

        if (success) {

            val noMedia = File(
                "$path/" + QuickInjectable.applicationContext2().resources.getString(
                    R.string.NO_MEDIA
                ))

            if (!noMedia.exists() && withNoMedia) {
                try {
                    noMedia.createNewFile()
                } catch (e: IOException) {
                    QuickLogWriter.printStackTrace(e)
                }
            }

            return "Success"
        }

        else return "Failure"
    }

    /**
     * get the bitmap from the image file
     * @param f: the file name
     * @param width: the width needed
     * @param height: the height needed
     * @return a bitmap file
     */
    fun decodeFile(f: String, width: Int, height: Int): Bitmap? {

        try {

            //decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)
            var scale = 1
            while (o.outWidth / scale / 2 >= width && o.outHeight / scale / 2 >= height)
                scale *= 2

            //decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }

        return null
    }

    /**
     * get a resized bitmap
     * @param bitmap: the bitmap original file
     * @param newHeight: the new height
     * @param newWidth: the new width
     * @param rotation: the rotation
     * @return a resized bitmap file
     */
    @Throws(Exception::class)
    fun getResizedBitmap(bitmap: Bitmap?, newHeight: Int, newWidth: Int, rotation: Int): Bitmap {

        val width = bitmap!!.width
        val height = bitmap.height
        newWidth.toFloat() / width
        newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.setRotate(rotation.toFloat())
        return  Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
    }

    @Throws(FileNotFoundException::class)
    fun unzipFile(fileName: String, fileLocation: String, destination: String): File? {

        val zis = ZipInputStream(BufferedInputStream(FileInputStream(File("$fileLocation/$fileName"))))
        var ze: ZipEntry? = null
        var count: Int? = null
        val buffer = ByteArray(8192)
        var unzippedFile: File? = null

        while ({ ze = zis.nextEntry; ze }() != null) {

            unzippedFile = File("$destination/${ze?.name}")

            val dir = if (ze?.isDirectory!!) unzippedFile else unzippedFile.parentFile

            if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)

            if (ze!!.isDirectory) continue

            FileOutputStream(unzippedFile).use { fileOutputStream ->

                while ({ count = zis.read(buffer); count }() != -1)
                    count?.let { fileOutputStream.write(buffer, 0, it) }
            }

            // if time should be restored as well
            val time = ze!!.time

            if (time > 0) unzippedFile.setLastModified(time)
        }

        zis.close()

        return if (unzippedFile !== null && unzippedFile.exists()) {

            QuickLogWriter.debugLogging("Un-zip Completed")
            unzippedFile
        } else {
            QuickLogWriter.errorLogging("Error", "the file was deleted or not properly downloaded")

            null
        }
    }

    @Throws(Exception::class)
    fun zipFolder(inputPath: String, outputPath: String, todayFilesOnly: Boolean = false, filesCreatedOnDay: String = "") {

        val oneDay = TimeUnit.DAYS.toMillis(1)
        val fos = FileOutputStream(outputPath)
        val zos = ZipOutputStream(fos)
        val srcFile = File(inputPath)
        val files = srcFile.listFiles()
        var initialDuration: Long = 0
        var toDuration: Long = 0
        val buffer = ByteArray(1024)

        if (!filesCreatedOnDay.isEmpty()) {

            val month = Calendar.getInstance().get(Calendar.MONTH) + 1
            val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH)
            initialDuration = sdf.parse("$filesCreatedOnDay/$month/" + Calendar.getInstance().get(Calendar.YEAR) + " 00:00:00").time
            toDuration = sdf.parse("$filesCreatedOnDay/$month/" + Calendar.getInstance().get(Calendar.YEAR) + " 23:59:00").time
        }

        for (file in files) {

            try {
                if (!filesCreatedOnDay.isEmpty()) {

                    if (file.lastModified() in initialDuration..(toDuration - 1)) zipFolderHelper(zos, file, buffer)
                } else if (todayFilesOnly) {

                    if (Date().time - file.lastModified() <= oneDay) zipFolderHelper(zos, file, buffer)
                } else zipFolderHelper(zos, file, buffer)
            }
            catch (e: Exception) {
                QuickLogWriter.errorLogging("Error", e.toString())
            }
        }

        zos.close()
    }

    private fun zipFolderHelper(zos: ZipOutputStream, file: File, buffer: ByteArray) {

        var length: Int? = null
        QuickLogWriter.debugLogging("Adding file: " + file.name)
        zos.putNextEntry(ZipEntry(file.name))
        FileInputStream(file).use { fileInputStream ->

            while ({ length = fileInputStream.read(buffer); length }() != -1)
                length?.let { zos.write(buffer, 0, it) }
        }
        zos.closeEntry()
    }
}
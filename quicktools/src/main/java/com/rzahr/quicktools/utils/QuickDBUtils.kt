@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import com.rzahr.quicktools.QuickInjectable
import com.rzahr.quicktools.QuickLogWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

/**
 * @author Rashad Zahr
 *
 * object used as a helper to build queries and to copy the database to the internal application directory
 */
object QuickDBUtils {

    /**
     * a simple SQL query select helper
     * @param columns: the columns selected
     * @param table: the table selected from
     * @param whereClause: the optional where clause
     * @param groupByClause: the optional group by clause
     * @param orderByClause: the optional order by clause
     * @return a query string from the provided attributes
     */
    fun simpleSelect(columns: String, table: String, whereClause: String = "", groupByClause: String = "", orderByClause: String = ""): String {

        var query = "SELECT $columns FROM $table"

        if (whereClause.isNotEmpty()) query += " WHERE $whereClause"

        if (groupByClause.isNotEmpty()) query+= " GROUP BY $groupByClause"

        if (orderByClause.isNotEmpty()) query+= " ORDER BY $orderByClause"

        QuickLogWriter.debugLogging(query)

        return query
    }

    /**
     * a complex SQL query select helper
     * @param columns: the columns selected
     * @param table: the table selected from
     * @param joins: the joins between the various tables
     * @param whereClause: the optional where clause
     * @param groupByClause: the optional group by clause
     * @return a query string from the provided attributes
     */
    fun complexSelect(columns: String, table: String, joins: Array<String>, whereClause: String = "", groupByClause: String = ""): String {

        var query = "SELECT $columns FROM $table "

        for (join in joins) query += "LEFT OUTER JOIN $join "

        if (whereClause.isNotEmpty()) query+= "WHERE $whereClause"

        if (groupByClause.isNotEmpty()) query+= "GROUP BY $groupByClause"

        QuickLogWriter.debugLogging(query)

        return query
    }

    /**
     * a simple distinct SQL query select helper
     * @param columns: the columns selected
     * @param table: the table selected from
     * @param whereClause: the optional where clause
     * @return a query string from the provided attributes
     */
    fun distinctSelect(columns: String, table: String, whereClause: String = ""): String {

        var query = "SELECT DISTINCT $columns FROM $table"

        if (whereClause.isNotEmpty()) query += " WHERE $whereClause"

        QuickLogWriter.debugLogging(query)

        return query
    }

    /**
     * returns if the database exists
     * @return boolean value representing if the database exist
     */
    fun databaseExist(): Boolean {

        val dbName = "Database.db"
        return if (QuickInjectable.applicationContext().getDatabasePath(dbName).exists()) true

        else {

            if (!Arrays.asList(*QuickInjectable.applicationContext().assets.list("")).contains(dbName)) return false

            QuickFileUtils.createDirectory(getDBPath(), false)

            val inputStream = QuickInjectable.applicationContext().assets.open(dbName)
            val out = FileOutputStream(File(getDBPath() + dbName))
            val buf = ByteArray(1024)
            while (inputStream.read(buf) > 0) out.write(buf)

            inputStream.close()
            out.flush()
            out.close()
            true
        }
    }

    /**
     * copies the database to the internal application directory
     * @param dbExternalPath: the file path that will be copied
     * @param downloadedDbName: the file name
     * @return boolean value representing a successful copy of the database
     */
    fun copyDatabaseFromExternalDirectory(dbExternalPath: String, downloadedDbName: String): Boolean {

        val outFileName = getDBPath() + downloadedDbName
        val fileDirectory = File(File(outFileName).parent)
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdirs()) {
                return false
            }
        }
        QuickFileUtils.createDirectory(
            getDBPath(), false
        )

        try {
            val inputStream = FileInputStream(dbExternalPath + downloadedDbName)
            val out = FileOutputStream(File(outFileName))
            val buf = ByteArray(1024)
            while (inputStream.read(buf) > 0) {
                out.write(buf)
            }

            inputStream.close()
            out.flush()
            out.close()
        }
        catch (e: FileNotFoundException){
            return false
        }

        return true
    }

    /**
     * gets the database path
     * @return db path
     */
    fun getDBPath(): String {

        return QuickInjectable.applicationContext().applicationInfo.dataDir + "/databases/"
    }
}
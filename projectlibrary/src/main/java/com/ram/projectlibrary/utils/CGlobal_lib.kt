package com.ram.projectlibrary.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.ram.projectlibrary.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ramashish Prajapati on 04-12-2019
 **/

class CGlobal_lib(context: Context?) {

    private lateinit var context: Context
    private lateinit var mEditorVersionPersistent: SharedPreferences.Editor
    private lateinit var mPrefsVersionPersistent: SharedPreferences

    init {
        try {
            if (context != null) {
                this.context = context
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private var instant: CGlobal_lib? = null
        private var authority: String? = null

        @Synchronized
        fun getInstance(context: Context?): CGlobal_lib? {

            if (instant == null) {
                instant = CGlobal_lib(context)
            }
            return instant
        }

    }

    fun getPersistentPreference(context: Context): SharedPreferences {

        mPrefsVersionPersistent = context.applicationContext
            .getSharedPreferences(
                CGlobal_Constant.SHARED_PREFERENCE_PRESISTENT,
                Context.MODE_PRIVATE
            )

        return mPrefsVersionPersistent
    }

    fun getPersistentPreferenceEditor(context: Context): SharedPreferences.Editor {
        mEditorVersionPersistent = getPersistentPreference(context).edit()
        return mEditorVersionPersistent
    }

    fun showMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun isDarkTheme(): Boolean? {

        var isNightMode = this.context.let {

            getInstance(it)?.getPersistentPreference(this.context)
                ?.getBoolean(CGlobal_Constant.IS_NIGHT_MODE, false)
        }
        return isNightMode
    }

    /** Use external media if it is available, our app's file directory otherwise */
    fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }


    fun toDate(it: String): Date {
        val dateFormat = "dd/MM/yyyy HH:mm:ss"
        val timeZone: TimeZone = TimeZone.getTimeZone("UTC")

        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(it)
    }

    fun formatTo(
        date: Date,
        dateFormat: String
    ): String {
        val timeZone: TimeZone = TimeZone.getDefault()

        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(date)
    }

    fun getDiffernceTiming(datetime: String): String {
        var postedTimeing: String
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

        val oldDate: Date = dateFormat.parse(datetime)
        val currentDate = Date()

        val diff: Long = currentDate.time - oldDate.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        if (seconds < 60) {
            postedTimeing = ""
            postedTimeing = seconds.toString() + "S AGO"
        } else if (minutes < 60) {
            postedTimeing = ""
            postedTimeing = minutes.toString() + "M AGO"
        } else if (hours < 24) {
            postedTimeing = ""
            postedTimeing = hours.toString() + "H AGO"
        } else {
            postedTimeing = ""
            postedTimeing = days.toString() + "D AGO"
        }
        Log.d("Date time", "second $seconds ,  Minutes $minutes,  Hours $hours,  Days $days")

        return postedTimeing
    }


    fun decodeBase64String(inputString: String): Bitmap {
        val decodedByte: ByteArray = Base64.decode(inputString, 0)
        return BitmapFactory
            .decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    fun convertBitmapIntoFile(bitmap: Bitmap): File {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        var byteArray = byteArrayOutputStream.toByteArray()

        var filterImagePath = FileNameCreation.createImageFile(this.context, "Filter_IC_")!!
        var fileOutputStream = FileOutputStream(filterImagePath)
        fileOutputStream.write(byteArray)
        fileOutputStream.flush()
        fileOutputStream.close()
        return filterImagePath
    }

}
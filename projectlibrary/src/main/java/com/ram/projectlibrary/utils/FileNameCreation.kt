package com.ram.projectlibrary.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ramashish Prajapati on 07,April,2020
 */
class FileNameCreation {
    /**
     * method to return File of created image
     * @param context of calling class
     * @return File of the created image
     */
    companion object {

        fun getOutputDirectory(context: Context): File {
            val appcontext = context.applicationContext
            val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absoluteFile
                ?: context.externalMediaDirs.first()?.let {
                    File(it, "Folow").apply { mkdirs() }
                }

            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appcontext.filesDir
        }

        fun createImageFile(context: Context, imagename: String): File? {
            var outputDirectory = getOutputDirectory(context)
            var imageFile: File? = null

            if (outputDirectory != null && outputDirectory.exists()) {

                try {
                    // Create an image file name
                    // val c: Calendar = Calendar.getInstance()
                    val timeStamp: String =
                        SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.getDefault()).format(Date())
                    val imageFileName = imagename + timeStamp + "_"

                    //now create jpg image under created directory
                    imageFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",  /* suffix */
                        outputDirectory /* directory */
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            return imageFile
        }
    }


}
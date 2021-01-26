package com.ram.mediapicker.utility

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.ram.mediapicker.model.PhotoModel
import java.io.File
import java.util.*

/**
 * Created by Ramashish Prajapati on 25,January,2021
 */
object FileSearch {

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    fun getFilePaths(context: Context, directory: String?): ArrayList<PhotoModel> {
        val pathArray = ArrayList<PhotoModel>()
        val file = File(directory)
        val listfiles = file.listFiles()
        if (!listfiles.isNullOrEmpty()) {
            for (i in listfiles.indices) {
                if (listfiles[i].isFile) {
                    var mediaType = getMimeType(context, listfiles[i].absolutePath)
                    pathArray.add(PhotoModel(listfiles[i].absolutePath, 0, false, mediaType))
                }
            }
        }
        return pathArray
    }

    fun getMimeType(context: Context, mediaPath: String): String? {

        val uri = Uri.fromFile(File(mediaPath))
        var mimeType: String? = null

        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = context.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                .toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase())
        }

        var substring = mimeType?.substringBefore("/")

        return substring
    }
}
package com.ram.mediapicker

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.ram.mediapicker.adapter.MediaAdapter
import com.ram.mediapicker.adapter.OnMediaSelected
import com.ram.mediapicker.model.FolderModel
import com.ram.mediapicker.model.PhotoModel
import com.ram.mediapicker.utility.AppConstants
import com.ram.mediapicker.utility.AppConstants.REQUEST_CODE_PERMISSIONS
import com.ram.mediapicker.utility.FileSearch
import com.ram.projectlibrary.utils.CGlobal_lib
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMediaSelected {

    companion object {
        // This is an array of the permission required for app.
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    /*To store and check for duplicate entry of folder from device*/
    var folderIdsList = ArrayList<String>()

    /*To store the folder path and name to get media content from specific folder*/
    val directoryNames = ArrayList<FolderModel>()

    /*To store the selected media content list for further uses*/
    private var selectedImageList: ArrayList<PhotoModel> = ArrayList()

    /*To store all the media content getting from directorynames array
    list to show in recyclerview list*/
    private var mediaList: ArrayList<PhotoModel> = ArrayList()

    /*Adapter to populate the media content in recyclerview*/
    private lateinit var mediaAdapter: MediaAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*To check the pemssion for app*/
        requestPermissions(
            REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )

        initRecyclerview()

        spinner_media_directory?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    try {
                        if (!directoryNames.isNullOrEmpty()) {
                            getallPhotos(directoryNames[position].folderPath + directoryNames[position].folderName)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    /*Check granted permission for app*/
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    /*Check granted permission for app*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initSpinner()
            } else {
                CGlobal_lib.getInstance(applicationContext)!!
                    .showMessage("Please grant the permission...")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /*To get all the image and video related folder from android device to show in toolbar spinner */
    private fun getMediaBuckets(mediaType: Int): ArrayList<FolderModel>? {
        val mediaBucketFolder = ArrayList<FolderModel>()
        /*Checking mediatype for image/video*/
        val uri: Uri = if (mediaType == AppConstants.IMAGE_TYPE) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        // which image properties are we querying
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )
        val cursor = this.contentResolver.query(uri, projection, null, null, null)
        if (mediaBucketFolder != null) {
            if (mediaBucketFolder.isNotEmpty()) {
                mediaBucketFolder.clear()
            }
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {

                var bucketName: String? = cursor.getString(
                    cursor.getColumnIndex(
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
                    )
                )
                var folderid: String? =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                var imagePath: String =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))

                if (!folderIdsList.contains(folderid)) {
                    folderIdsList.add(folderid!!)
                    val splitImagePath = imagePath.split(bucketName!!.toRegex()).toTypedArray()
                    val folderPath = splitImagePath[0]
                    mediaBucketFolder.add(FolderModel(bucketName, folderPath))
                    Log.d("bucket", "" + mediaBucketFolder)
                }
            }
            cursor.close()
        }
        return mediaBucketFolder
    }

    /*Initializing spinner to show the folder getting from device*/
    private fun initSpinner() {
        getMediaBuckets(AppConstants.IMAGE_TYPE)?.let { directoryNames.addAll(it) }
        getMediaBuckets(AppConstants.VIDEO_TYPE)?.let { directoryNames.addAll(it) }

        if (!directoryNames.isNullOrEmpty()) {
            var spinnerValue = ArrayList<String>()
            for (foldername in directoryNames) {
                spinnerValue.add(foldername.folderName)
            }
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, spinnerValue
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_media_directory.adapter = adapter
        }
    }

    /*Initializing the recyclerview to show the list of media content*/
    private fun initRecyclerview() {
        rv_medialist.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            setHasFixedSize(true)
            mediaAdapter = MediaAdapter(this@MainActivity)
            adapter = mediaAdapter
        }
    }

    /*fetching all the image/video from selected folder */
    private fun getallPhotos(selectedDirectory: String) {
        if (!selectedImageList.isNullOrEmpty()) {
            selectedImageList.clear()
        }
        if (!mediaList.isNullOrEmpty())
            mediaList.clear()

        mediaList = FileSearch.getFilePaths(this, selectedDirectory)
        /*Adding fetching media to recyclerview list*/
        if (!mediaList.isNullOrEmpty()) {
            mediaAdapter.submitList(mediaList)
            mediaAdapter.notifyDataSetChanged()
        }
    }

    // Adding image in selectedImageList
    private fun selectImage(position: Int) {
        try {
            // Check before add new item in ArrayList;
            mediaList[position].isSelected = true
            mediaList[position].selectedPostion = 0
            /*all the selected media list*/
            selectedImageList.add(mediaList[position])
            setCountOnImage(position)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Removeing image from selectedImageList
    private fun unSelectImage(position: Int) {
        try {
            for (i in selectedImageList.indices) {
                if (mediaList[position].photoPath != null) {
                    if (selectedImageList[i].photoPath == mediaList[position].photoPath) {
                        mediaList[position].isSelected = false
                        mediaList[position].selectedPostion = 0
                        selectedImageList.removeAt(i)
                        break
                    }
                }
            }
            resetCountOnImage(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*Reset the count of selected image after removing from selected list */
    private fun resetCountOnImage(position: Int) {
        try {
            if (!selectedImageList.isNullOrEmpty()) {
                var count = 0
                for (j in selectedImageList.indices) {
                    for (i in mediaList.indices) {
                        if (selectedImageList[j].photoPath == mediaList[i].photoPath) {
                            count += 1
                            mediaList[i].selectedPostion = count
                            break
                        }
                    }
                }
            }
            mediaAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*Show the count and increment on selected images */
    private fun setCountOnImage(position: Int) {
        try {
            for (i in selectedImageList.indices) {
                if (selectedImageList[i].photoPath == mediaList[position].photoPath) {
                    mediaList[position].selectedPostion = i + 1
                }
            }
            mediaAdapter.notifyItemChanged(position)
        } catch (e: Exception) {
        }
    }

    /*Adding/removing selected image from selecte list*/
    override fun onSingleClick(position: Int, photoModel: PhotoModel) {
        if (!selectedImageList.isNullOrEmpty()) {
            checkIsSelected(position, photoModel)
        }
    }

    /*Adding/removing selected image from selecte list*/
    override fun onLongPress(position: Int, photoModel: PhotoModel) {
        checkIsSelected(position, photoModel)
    }

    /*Checking if selected image is already selected or not to call selectImage or unSelectImage fun */
    private fun checkIsSelected(position: Int, photoModel: PhotoModel) {
        try {
            if (!mediaList[position].isSelected) {
                selectImage(position)
            } else {
                unSelectImage(position)
            }
        } catch (ed: ArrayIndexOutOfBoundsException) {
            ed.printStackTrace()
        }
    }

}
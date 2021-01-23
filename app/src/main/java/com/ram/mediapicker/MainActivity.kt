package com.ram.mediapicker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.ram.mediapicker.utility.AppConstants
import com.ram.projectlibrary.utils.CGlobal_lib

class MainActivity : AppCompatActivity() {

    companion object {
        // This is an array of the permission required for app.
        private val REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    /*Check granted permission for app*/
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    /*Check granted permission for app*/
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == AppConstants.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
            } else {
                CGlobal_lib.getInstance(applicationContext)!!
                        .showMessage("Please grant the permission...")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
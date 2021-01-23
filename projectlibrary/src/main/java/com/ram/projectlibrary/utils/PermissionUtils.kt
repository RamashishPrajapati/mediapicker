package com.ram.projectlibrary.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ram.projectlibrary.interfaces.PermissionResultCallback

/**
 * Created by Ramashish Prajapati on 04-12-2019
 **/
class PermissionUtils(context: Context) {
    private var current_activity: Activity
    private var permissionResultCallback: PermissionResultCallback
    private var permissionList = ArrayList<String>()
    private var listPermissionNeeded = ArrayList<String>()
    private var dialogContent = ""
    private var reqCode = 0

    init {
        current_activity = context as Activity
        permissionResultCallback = context as PermissionResultCallback
    }

    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param dialog_content
     * @param request_code
     */
    fun checkPermission(
        permissionList: ArrayList<String>,
        dialogContent: String,
        requestCode: Int
    ) {

        this.permissionList = permissionList
        this.dialogContent = dialogContent
        this.reqCode = reqCode

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissionList, requestCode)) {
                permissionResultCallback.PermissionGranted(requestCode)
                Log.i("all permissions", "granted")
                Log.i("proceed", "to callback")
            } else {
                permissionResultCallback.PermissionGranted(requestCode)
                Log.i("all permissions", "granted")
                Log.i("proceed", "to callback")
            }
        }
    }

    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param request_code
     * @return
     */

    private fun checkAndRequestPermissions(
        permissionList: ArrayList<String>,
        requestCode: Int
    ): Boolean {
        if (permissionList.size > 0) {
            listPermissionNeeded = ArrayList()
            for (i in permissionList.indices) {
                val hasPermission =
                    ContextCompat.checkSelfPermission(current_activity, permissionList[i])
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionNeeded.add(permissionList[i])
                }
            }
            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    current_activity,
                    listPermissionNeeded.toTypedArray(),
                    requestCode
                )
                return false
            }
        }
        return true
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        permissionList: Array<String>,
        grantResult: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResult.size > 0) {
                var perms: MutableMap<String, Int> = HashMap()
                for (i in permissionList.indices) {
                    perms.put(permissionList[i], grantResult[i])
                }

                val pendingPermissions = ArrayList<String>()
                for (i in listPermissionNeeded.indices) {
                    if (perms.get(listPermissionNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                current_activity,
                                listPermissionNeeded.get(i)
                            )
                        ) {
                            pendingPermissions.add(listPermissionNeeded.get(i))
                        } else {
                            Log.i("Go to settings", "and enable permissions")
                            permissionResultCallback.NeverAskAgain(requestCode)
                            Toast.makeText(
                                current_activity,
                                "Go to settings and enable permissions",
                                Toast.LENGTH_LONG
                            ).show()
                            return

                        }
                    }
                }

                if (pendingPermissions.size > 0) {
                    showMessageOKCancel(
                        dialogContent,
                        DialogInterface.OnClickListener { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> checkPermission(
                                    this.permissionList,
                                    dialogContent,
                                    requestCode
                                )
                                DialogInterface.BUTTON_NEGATIVE -> {
                                    Log.i("", "")
                                    if (this.permissionList.size == pendingPermissions.size) permissionResultCallback.PermissionDenied(
                                        requestCode
                                    )
                                    else permissionResultCallback.PartialPermissionGranted(
                                        requestCode,
                                        pendingPermissions
                                    )
                                }
                            }

                        })
                } else {
                    Log.i("all", "permissions granted")
                    Log.i("proceed", "to next step")
                    permissionResultCallback.PermissionGranted(requestCode)
                }

            }


        }
    }

    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private fun showMessageOKCancel(
        message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(current_activity)
            .setMessage(message)
            .setPositiveButton("Ok", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }
}
package com.ram.projectlibrary.interfaces

/**
 * Created by Ramashish Prajapati on 04-12-2019
 **/
interface PermissionResultCallback {
    fun PermissionGranted(request_code: Int)
    fun PartialPermissionGranted(request_code: Int, granted_permission: ArrayList<String>)
    fun PermissionDenied(request_code: Int)
    fun NeverAskAgain(request_code: Int)
}
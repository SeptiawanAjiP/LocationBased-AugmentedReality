package com.dewakoding.arlocationbased.util

import pub.devrel.easypermissions.EasyPermissions
import android.Manifest
import android.content.Context
import android.os.Build

object CameraUtility {
    fun hasCameraPermissions(context: Context) =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.CAMERA
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.CAMERA
            )
        }
}
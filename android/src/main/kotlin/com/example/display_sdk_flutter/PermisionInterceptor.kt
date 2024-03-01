package com.example.display_sdk_flutter

import android.app.Activity
import android.widget.Toast
import com.hjq.permissions.IPermissionInterceptor
import com.hjq.permissions.OnPermissionCallback

class PermissionInterceptor : IPermissionInterceptor {
    override fun grantedPermissions(
        activity: Activity, allPermissions: List<String>, grantedPermissions: List<String>,
        all: Boolean, callback: OnPermissionCallback
    ) {
        callback.onGranted(grantedPermissions, all)
    }

    override fun deniedPermissions(
        activity: Activity, allPermissions: List<String>, deniedPermissions: List<String>,
        never: Boolean, callback: OnPermissionCallback
    ) {
        callback.onDenied(deniedPermissions, never)
        Toast.makeText(activity, "permission fail",Toast.LENGTH_SHORT).show()
    }
}
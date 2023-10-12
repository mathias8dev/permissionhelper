package com.mathias8dev.permissionhelper.permission


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.activity.result.ActivityResultCaller
import androidx.core.content.ContextCompat


/**
 * Find the closest Activity in a given Context.
 */
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

internal fun Context.findActivityResultCaller(): ActivityResultCaller {
    val activity = this.findActivity()
    if (activity is ActivityResultCaller) return activity
    else throw IllegalStateException(
        "Permissions should be called in the context of an activity " +
                "that implements the ActivityResultCaller interface"
    )
}

internal fun Context.checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

internal fun Activity.shouldShowRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}


fun Context.openAppSystemSettings() {
    this.findActivity().startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.UUID


/**
 * Find the closest Activity in a given Context.
 */
internal fun Context.findActivity(): Activity {
    return findOwner<Activity>(this) ?: throw IllegalStateException(
        "Permissions should be called in the context of an Activity"
    )
}

internal fun Context.findActivityResultCaller(): ActivityResultCaller {
    return findOwner<ActivityResultCaller>(this) ?: throw IllegalStateException(
        "Permissions should be called in the context of an activity " +
                "that implements the ActivityResultCaller interface"
    )
}

internal fun Context.findActivityRegistryOwner(): ActivityResultRegistryOwner {

    return findOwner<ActivityResultRegistryOwner>(this) ?: throw IllegalStateException(
        "Permissions should be called in the context of an activity " +
                "that implements the ActivityResultRegistryOwner interface"
    )
}

internal inline fun <reified T> findOwner(context: Context): T? {
    var innerContext = context
    while (innerContext is ContextWrapper) {
        if (innerContext is T) {
            return innerContext
        }
        innerContext = innerContext.baseContext
    }
    return null
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


fun Context.openStorageSystemSettings() {
    this.findActivity().startActivity(Intent().apply {
        action = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R)
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        else Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}


fun <I, O> Context.registerActivityLauncher(
    contract: ActivityResultContract<I, O>,
    callback: (O) -> Unit,
): ActivityResultLauncher<I> {
    val key = UUID.randomUUID().toString()
    val activityResultRegistry = findActivityRegistryOwner().activityResultRegistry
    val launcher = activityResultRegistry.register(key, contract) {
        callback(it)
    }
    val lifecycleOwner = findOwner<LifecycleOwner>(this)
    lifecycleOwner!!.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                launcher.unregister()
            }
        }
    })

    return launcher
}
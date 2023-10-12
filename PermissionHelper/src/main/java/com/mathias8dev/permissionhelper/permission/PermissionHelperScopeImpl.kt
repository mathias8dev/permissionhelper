package com.mathias8dev.permissionhelper.permission

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



@Stable
internal class PermissionHelperScopeImpl internal constructor(
    private val permission: Permission,
    private val context: Context
) : PermissionHelperScope {

    private val _permissionRequestEvent =
        MutableStateFlow<PermissionRequestEvent>(PermissionRequestEvent.Idle)
    val permissionRequestEvent = _permissionRequestEvent.asStateFlow()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var onPermissionResult: ((PermissionState) -> Unit)? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    init {
        setupPermissionLauncher()
    }

    private fun setupPermissionLauncher() {
        requestPermissionLauncher = context.findActivityResultCaller().registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) onPermissionResult?.invoke(PermissionState.Granted)
            else if (context.findActivity().shouldShowRationale(permission.manifestKey))
                onPermissionResult?.invoke(PermissionState.Denied)
            else {
                coroutineScope.launch {
                    _permissionRequestEvent.emit(
                        PermissionRequestEvent.OnGotoSettings { proceed ->
                            emitIdle()
                            if (proceed) context.openAppSystemSettings()
                            else onPermissionResult?.invoke(PermissionState.Denied)
                        }
                    )
                }
            }
        }
    }

    private fun emitIdle() {
        coroutineScope.launch {
            PermissionRequestEvent.Idle
        }
    }


    override fun getPermissionState(): PermissionState {
        return if (context.checkPermission(permission.manifestKey)) PermissionState.Granted
        else if (context.findActivity().shouldShowRationale(permission.manifestKey))
            PermissionState.ShowRationale
        else PermissionState.Denied
    }


    override fun launchPermission(onPermissionResult: (PermissionState) -> Unit) {
        coroutineScope.launch {

            this@PermissionHelperScopeImpl.onPermissionResult = onPermissionResult
            val activity = context.findActivity()
            if (activity.checkPermission(permission.manifestKey)) {
                onPermissionResult(PermissionState.Granted)
            } else if (activity.shouldShowRationale(permission.manifestKey)) {
                _permissionRequestEvent.emit(
                    PermissionRequestEvent.OnShowRationale { proceed ->
                        emitIdle()
                        if (proceed) requestPermissionLauncher.launch(
                            permission.manifestKey
                        ) else onPermissionResult(PermissionState.Denied)
                    }
                )
            } else {
                _permissionRequestEvent.emit(
                    PermissionRequestEvent.OnRequestPermission { proceed ->
                        emitIdle()
                        if (proceed) requestPermissionLauncher.launch(
                            permission.manifestKey
                        ) else onPermissionResult(PermissionState.Denied)
                    }
                )
            }
        }
    }

}
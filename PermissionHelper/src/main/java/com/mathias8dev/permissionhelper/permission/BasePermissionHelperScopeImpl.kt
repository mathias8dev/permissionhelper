package com.mathias8dev.permissionhelper.permission

import android.Manifest
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@Stable
internal open class BasePermissionHelperScopeImpl(
    protected val context: Context
) : PermissionHelperScope, RememberObserver {
    protected val _permissionRequestEvent =
        MutableStateFlow<PermissionRequestEvent>(PermissionRequestEvent.Idle)
    val permissionRequestEvent = _permissionRequestEvent.asStateFlow()
    protected val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    protected var onPermissionResult: ((PermissionState) -> Unit)? = null
    protected lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    protected lateinit var currentLaunchedPermission: Permission


    private fun setupPermissionLauncher() {
        requestPermissionLauncher = context.registerActivityLauncher(
            ActivityResultContracts.RequestPermission()
        ) {
            checkIfCurrentPermissionIsInitialized()
            if (it) onPermissionResult?.invoke(PermissionState.Granted)
            else if (context.findActivity()
                    .shouldShowRationale(currentLaunchedPermission.manifestKey)
            )
                onPermissionResult?.invoke(PermissionState.Denied)
            else {
                coroutineScope.launch {
                    _permissionRequestEvent.emit(
                        PermissionRequestEvent.OnGotoSettings(currentLaunchedPermission) { proceed ->
                            emitIdle()
                            if (proceed) {
                                if (currentLaunchedPermission.manifestKey == Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                                    context.openStorageSystemSettings()
                                else context.openAppSystemSettings()
                            } else {
                                onPermissionResult?.invoke(PermissionState.Denied)
                            }
                        }
                    )
                }
            }
        }
    }

    protected fun emitIdle() {
        Log.d("PermissionScopeImpl", "Try emitting idle")
        coroutineScope.launch {
            _permissionRequestEvent.emit(
                PermissionRequestEvent.Idle
            )
        }
    }

    private fun checkIfCurrentPermissionIsInitialized() {
        if (!::currentLaunchedPermission.isInitialized) throw IllegalStateException(
            "CurrentLaunchedPermission should be updated to reflect the launched permission" +
                    " before each launch request"
        )
    }

    protected fun permissionStateOf(permission: Permission): PermissionState {
        return if (context.checkPermission(permission.manifestKey)) PermissionState.Granted
        else PermissionState.Denied
    }


    override fun getPermissionState(): PermissionState {
        checkIfCurrentPermissionIsInitialized()
       return permissionStateOf(currentLaunchedPermission)
    }


    override fun launchPermission(onPermissionResult: (PermissionState) -> Unit) {
        checkIfCurrentPermissionIsInitialized()
        coroutineScope.launch {
            this@BasePermissionHelperScopeImpl.onPermissionResult = onPermissionResult
            val activity = context.findActivity()
            if (permissionStateOf(currentLaunchedPermission) == PermissionState.Granted) {
                onPermissionResult(PermissionState.Granted)
            } else if (activity.shouldShowRationale(currentLaunchedPermission.manifestKey)) {
                _permissionRequestEvent.emit(
                    PermissionRequestEvent.OnShowRationale(currentLaunchedPermission) { proceed ->
                        emitIdle()
                        if (proceed) requestPermissionLauncher.launch(
                            currentLaunchedPermission.manifestKey
                        ) else onPermissionResult(PermissionState.Denied)
                    }
                )
            } else {
                _permissionRequestEvent.emit(
                    PermissionRequestEvent.OnRequestPermission(currentLaunchedPermission) { proceed ->
                        emitIdle()
                        if (proceed) requestPermissionLauncher.launch(
                            currentLaunchedPermission.manifestKey
                        ) else onPermissionResult(PermissionState.Denied)
                    }
                )
            }
        }
    }

    override fun onAbandoned() {
        // Nothing to do as [onRemembered] was not called.
    }

    override fun onForgotten() {
        if (::requestPermissionLauncher.isInitialized) {
            requestPermissionLauncher.unregister()
        }
    }

    override fun onRemembered() {
        setupPermissionLauncher()
    }
}
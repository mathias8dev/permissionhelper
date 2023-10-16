package com.mathias8dev.permissionhelper.permission

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO Refactor this class

internal open class BasePermissionHelperScopeImpl(
    protected val context: Context
): PermissionHelperScope {
    protected val _permissionRequestEvent =
        MutableStateFlow<PermissionRequestEvent>(PermissionRequestEvent.Idle)
    val permissionRequestEvent = _permissionRequestEvent.asStateFlow()
    protected val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    protected var onPermissionResult: ((PermissionState) -> Unit)? = null
    protected lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    protected lateinit var currentLaunchedPermission: Permission

    init {
        setupPermissionLauncher()
    }

    private fun setupPermissionLauncher() {
        requestPermissionLauncher = context.registerActivityLauncher(
            ActivityResultContracts.RequestPermission()
        ) {
            checkIfCurrentPermissionIsInitialized()
            if (it) onPermissionResult?.invoke(PermissionState.Granted)
            else if (context.findActivity().shouldShowRationale(currentLaunchedPermission.manifestKey))
                onPermissionResult?.invoke(PermissionState.Denied)
            else {
                coroutineScope.launch {
                    _permissionRequestEvent.emit(
                        PermissionRequestEvent.OnGotoSettings(currentLaunchedPermission) { proceed ->
                            emitIdle()
                            if (proceed) context.openAppSystemSettings()
                            else onPermissionResult?.invoke(PermissionState.Denied)
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


    override fun getPermissionState(): PermissionState {
        checkIfCurrentPermissionIsInitialized()
        return if (context.checkPermission(currentLaunchedPermission.manifestKey)) PermissionState.Granted
        else if (context.findActivity().shouldShowRationale(currentLaunchedPermission.manifestKey))
            PermissionState.ShowRationale
        else PermissionState.Denied
    }


    override fun launchPermission(onPermissionResult: (PermissionState) -> Unit) {
        checkIfCurrentPermissionIsInitialized()
        coroutineScope.launch {
            this@BasePermissionHelperScopeImpl.onPermissionResult = onPermissionResult
            val activity = context.findActivity()
            if (activity.checkPermission(currentLaunchedPermission.manifestKey)) {
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
}
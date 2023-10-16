package com.mathias8dev.permissionhelper.permission

import android.content.Context
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
internal class OneShotPermissionsHelperScopeImpl(
    permissions: List<Permission>,
    val context: Context
) : OneShotPermissionsHelperScope, RememberObserver {

    private val _permissionRequestEvent =
        MutableStateFlow<OneShotPermissionsRequestEvent>(OneShotPermissionsRequestEvent.Idle)
    val permissionRequestEvent = _permissionRequestEvent.asStateFlow()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var onPermissionsResult: ((List<Pair<Permission, PermissionState>>) -> Unit)? = null
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    private val declaredPermissions = permissions.distinct()
    private var launchedPermissions = declaredPermissions


    private fun getResults() = launchedPermissions.map { launchedPermission ->
        if (context.checkPermission(launchedPermission.manifestKey))
            launchedPermission to PermissionState.Granted
        else launchedPermission to PermissionState.Denied
    }

    private fun setupPermissionLauncher() {
        requestPermissionsLauncher = context.registerActivityLauncher(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) {
            val results = getResults()
            val activity = context.findActivity()
            val notGranted = it.filter { pair -> !pair.value }.map { pair -> pair.key }
            if (notGranted.isEmpty()) onPermissionsResult?.invoke(results)
            else if (notGranted.find { manifestKey ->
                    activity.shouldShowRationale(manifestKey)
                } != null)
                onPermissionsResult?.invoke(results)
            else {
                coroutineScope.launch {
                    _permissionRequestEvent.emit(
                        OneShotPermissionsRequestEvent.OnGotoSettings(
                            declaredPermissions.filter { key -> notGranted.contains(key.manifestKey) }
                        ) { proceed ->
                            emitIdle()
                            if (proceed) context.openAppSystemSettings()
                            else onPermissionsResult?.invoke(results)
                        }
                    )
                }
            }
        }
    }

    private fun emitIdle() {
        Log.d("PermissionScopeImpl", "Try emitting idle")
        coroutineScope.launch {
            _permissionRequestEvent.emit(
                OneShotPermissionsRequestEvent.Idle
            )
        }
    }


    private fun permissionsState(permissions: List<Permission>): List<Pair<Permission, PermissionState>> {
        return permissions.map {
            if (context.checkPermission(it.manifestKey)) {
                it to PermissionState.Granted
            } else {
                it to PermissionState.Denied
            }
        }
    }

    override fun getPermissionState(permissions: List<Permission>): List<Pair<Permission, PermissionState>> {
        return getPermissionStateByKeys(permissions.map { it.manifestKey })
    }

    override fun getPermissionStateByKeys(permissionManifestKeys: List<String>): List<Pair<Permission, PermissionState>> {
        val foundPermissions =
            declaredPermissions.filter { permissionManifestKeys.contains(it.manifestKey) }
        return permissionsState(foundPermissions)
    }

    override fun launchPermissions(onPermissionsResult: (List<Pair<Permission, PermissionState>>) -> Unit) {
        launchPermissions(declaredPermissions, onPermissionsResult)
    }

    override fun launchPermissions(
        permissions: List<Permission>,
        onPermissionsResult: (List<Pair<Permission, PermissionState>>) -> Unit
    ) {
        launchedPermissions = permissions.distinct()
        this.onPermissionsResult = onPermissionsResult
        coroutineScope.launch {

            val activity = context.findActivity()
            val results = getResults()

            val deniedPermissions = launchedPermissions.filter {
                !context.checkPermission(it.manifestKey)
            }

            val rationalePermissions = launchedPermissions.filter {
                activity.shouldShowRationale(it.manifestKey)
            }
            if (deniedPermissions.isEmpty()) {
                onPermissionsResult(results)
            } else if (rationalePermissions.isNotEmpty()) {
                _permissionRequestEvent.emit(
                    OneShotPermissionsRequestEvent.OnShowRationale(rationalePermissions) { proceed ->
                        emitIdle()
                        if (proceed) requestPermissionsLauncher.launch(
                            deniedPermissions.map { it.manifestKey }.toTypedArray()
                        ) else onPermissionsResult(results)
                    }
                )
            } else {
                _permissionRequestEvent.emit(
                    OneShotPermissionsRequestEvent.OnRequestPermission(deniedPermissions) { proceed ->
                        emitIdle()
                        if (proceed) requestPermissionsLauncher.launch(
                            rationalePermissions.map { it.manifestKey }.toTypedArray()
                        ) else onPermissionsResult(results)
                    }
                )
            }
        }

    }

    override fun onAbandoned() {
        // Nothing to do as [onRemembered] was not called.
    }

    override fun onForgotten() {
       if (::requestPermissionsLauncher.isInitialized) {
           requestPermissionsLauncher.unregister()
       }
    }

    override fun onRemembered() {
        setupPermissionLauncher()
    }

}
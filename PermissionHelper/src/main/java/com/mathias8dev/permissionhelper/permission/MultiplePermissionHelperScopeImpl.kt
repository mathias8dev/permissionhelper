package com.mathias8dev.permissionhelper.permission

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


internal class MultiplePermissionHelperScopeImpl internal constructor(
    permissions: Collection<Permission>,
    context: Context
) : BasePermissionHelperScopeImpl(context), MultiplePermissionHelperScope {
    private var launchStrategy: PermissionLaunchStrategy? = null
    private val declaredPermissions = permissions.distinct()

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
        val launchedPermissionsResult = mutableListOf<Pair<Permission, PermissionState>>()
        val declaredPermissions = permissions.distinct()
        var permissionWasAlreadyGranted = false
        var lastConfig: PermissionConfig? = null
        var index = 0
        fun recursivePermissionLaunch() {
            if (index >= declaredPermissions.size) {
                onPermissionsResult(launchedPermissionsResult)
                return
            } else {
                val permission = declaredPermissions[index]
                val permissionConfig = launchStrategy.getConfig(permission)
                coroutineScope.launch {
                    lastConfig?.let { oldConfig ->
                        if (!permissionWasAlreadyGranted || !oldConfig.skipConfigIfAlreadyGranted) {
                            oldConfig.delayForNextRequest?.let {
                                delay(it)
                            }
                            oldConfig.suspendedCall?.invoke()
                        }
                    }
                    lastConfig = permissionConfig
                    if (context.checkPermission(permission.manifestKey)) {
                        launchedPermissionsResult.add(permission to PermissionState.Granted)
                        permissionWasAlreadyGranted = true
                        index++
                        recursivePermissionLaunch()
                    } else {
                        permissionWasAlreadyGranted = false
                    }
                    val failedPermission =
                        permissionConfig.permissionsToCheck.find { !context.checkPermission(it.manifestKey) }
                    if (failedPermission != null) {
                        val deniedPermissions =
                            declaredPermissions.subList(index, declaredPermissions.size)
                                .map { it to PermissionState.Denied }
                        launchedPermissionsResult.addAll(deniedPermissions)
                        index = declaredPermissions.size
                        recursivePermissionLaunch()
                    }
                    currentLaunchedPermission = permission
                    launchPermission { permissionState ->
                        if (permissionState.isDenied && permissionConfig.abortOnFail) {
                            val deniedPermissions =
                                declaredPermissions.subList(index, declaredPermissions.size)
                                    .map { it to PermissionState.Denied }
                            launchedPermissionsResult.addAll(deniedPermissions)
                            index = declaredPermissions.size
                            recursivePermissionLaunch()
                        } else {
                            index++
                            launchedPermissionsResult.add(permission to permissionState)
                            recursivePermissionLaunch()
                        }
                    }
                }
            }

        }

        recursivePermissionLaunch()
    }

    override fun launchStrategy(
        strategyProvider: (permissions: Collection<Permission>) -> PermissionLaunchStrategy?
    ) {
        launchStrategy = strategyProvider(declaredPermissions)
    }
}
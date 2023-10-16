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
        coroutineScope.launch {
            var index = 0
            var permissionWasAlreadyGranted = false
            var lastConfig: PermissionConfig? = null
            while (index < declaredPermissions.size) {
                val permission = declaredPermissions[index]
                val permissionConfig = launchStrategy.getConfig(permission)
                lastConfig?.let { oldConfig ->
                    if (!permissionWasAlreadyGranted || !oldConfig.skipConfigIfAlreadyGranted) {
                        oldConfig.delayForNextRequest?.let {
                            delay(it)
                        }
                        oldConfig.suspendedCall?.invoke()
                    }
                }
                if (context.checkPermission(permission.manifestKey)) {
                    launchedPermissionsResult.add(permission to PermissionState.Granted)
                    permissionWasAlreadyGranted = true
                    index++
                    continue
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
                    continue
                }
                currentLaunchedPermission = permission
                launchPermission { permissionState ->
                    launchedPermissionsResult.add(permission to permissionState)
                    if (permissionState.isDenied && permissionConfig.abortOnFail) {
                        val deniedPermissions =
                            declaredPermissions.subList(index + 1, declaredPermissions.size)
                                .map { it to PermissionState.Denied }
                        launchedPermissionsResult.addAll(deniedPermissions)
                        index = declaredPermissions.size
                    }
                }

                lastConfig = permissionConfig
                index++
            }
            onPermissionsResult(launchedPermissionsResult)
        }

    }

    override fun launchStrategy(
        strategyProvider: (permissions: Collection<Permission>) -> PermissionLaunchStrategy?
    ) {
        launchStrategy = strategyProvider(declaredPermissions)
    }
}
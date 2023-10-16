package com.mathias8dev.permissionhelper.permission

import android.content.Context


class MultiplePermissionHelperScopeImpl internal constructor(
    permissions: List<Permission>,
    private val context: Context
) : MultiplePermissionHelperScope {
    private var launchStrategy: PermissionLaunchStrategy? = null
    private val declaredPermissions = permissions.toSet()

    private fun getPermissionState(permissions: Set<Permission>): List<Pair<Permission, PermissionState>> {
        return permissions.map {
            if (context.checkPermission(it.manifestKey)) {
                it to PermissionState.Granted
            } else {
                it to PermissionState.Denied
            }
        }
    }

    override fun getPermissionState(permissions: List<Permission>): List<Pair<Permission, PermissionState>> {
        return getPermissionState(
            declaredPermissions.intersect(
                permissions.toSet()
            )
        )
    }

    override fun getPermissionStateByKeys(permissionManifestKeys: List<String>): List<Pair<Permission, PermissionState>> {
        val foundPermissions = mutableSetOf<Permission>()
        declaredPermissions.forEach {
            if (permissionManifestKeys.contains(it.manifestKey)) foundPermissions.add(it)
        }
        return getPermissionState(foundPermissions)
    }

    override suspend fun launchPermission(): List<Pair<Permission, PermissionState>> {

        TODO("Not yet implemented")
    }

    override fun launchStrategy(
        strategyProvider: (permissions: Collection<Permission>) -> PermissionLaunchStrategy?
    ) {
        launchStrategy = strategyProvider(declaredPermissions)
    }
}
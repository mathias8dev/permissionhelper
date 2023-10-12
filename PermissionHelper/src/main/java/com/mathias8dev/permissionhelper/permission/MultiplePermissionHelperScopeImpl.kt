package com.mathias8dev.permissionhelper.permission


class MultiplePermissionHelperScopeImpl: MultiplePermissionHelperScope {
    private var launchStrategy: PermissionLaunchStrategy? = null

    override fun getPermissionState(permissions: List<Permission>): List<Pair<Permission, PermissionState>> {
        TODO("Not yet implemented")
    }

    override fun getPermissionState(permissionManifestKeys: List<String>): List<Pair<Permission, PermissionState>> {
        TODO("Not yet implemented")
    }

    override suspend fun launchPermission(): List<Pair<Permission, PermissionState>> {
        TODO("Not yet implemented")
    }

    override fun launchStrategy(
        permissions: List<Permission>,
        strategyProvider: (permissions: List<Permission>) -> PermissionLaunchStrategy?
    ) {
        launchStrategy = strategyProvider(permissions)
    }
}
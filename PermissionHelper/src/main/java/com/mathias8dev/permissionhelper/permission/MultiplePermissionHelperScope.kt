package com.mathias8dev.permissionhelper.permission


interface MultiplePermissionHelperScope {

    /**
     * Return the corresponding [PermissionState] corresponding to the provided permissions
     */
    fun getPermissionState(permissions: List<Permission>): List<Pair<Permission, PermissionState>>

    /**
     * Return the corresponding [PermissionState] corresponding to the provided permissionManifestKeys
     */
    fun getPermissionStateByKeys(permissionManifestKeys: List<String>): List<Pair<Permission, PermissionState>>

    /**
     * Used to launch permissions. Permissions are launched according to the defined launch strategy
     */
    fun launchPermissions(
        onPermissionsResult: (List<Pair<Permission, PermissionState>>)->Unit
    )

    fun launchPermissions(
        permissions: List<Permission>,
        onPermissionsResult: (List<Pair<Permission, PermissionState>>)->Unit
    )

    /**
     * Provide [PermissionLaunchStrategy] to the [MultiplePermissionHelperScope] to use when
     * permissions are launched.
     */
    fun launchStrategy(
        strategyProvider: (permissions: Collection<Permission>) -> PermissionLaunchStrategy?
    )
}

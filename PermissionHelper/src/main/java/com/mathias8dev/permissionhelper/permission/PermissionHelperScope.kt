package com.mathias8dev.permissionhelper.permission


interface PermissionHelperScope {

    /**
     * Return the corresponding [PermissionState] corresponding to the provided permission
     */
    fun getPermissionState(): PermissionState

    /**
     * Used to launch permissions.
     */
    fun launchPermission(
        onPermissionResult: (PermissionState) -> Unit
    )
}


package com.mathias8dev.permissionhelper.permission



internal sealed interface PermissionRequestEvent {
    class OnShowRationale(
        val permission: Permission,
        val onProceed: (Boolean) -> Unit
    ) : PermissionRequestEvent
    class OnRequestPermission(
        val permission: Permission,
        val onProceed: (Boolean) -> Unit
    ) : PermissionRequestEvent
    class OnPermissionGranted(
        val permission: Permission,
        val onProceed: () -> Unit
    ) : PermissionRequestEvent
    class OnGotoSettings(
        val permission: Permission,
        val onProceed: (Boolean) -> Unit
    ) : PermissionRequestEvent
    object Idle : PermissionRequestEvent
}

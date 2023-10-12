package com.mathias8dev.permissionhelper.permission



internal sealed interface PermissionRequestEvent {
    class OnShowRationale(val onProceed: (Boolean) -> Unit) : PermissionRequestEvent
    class OnRequestPermission(val onProceed: (Boolean) -> Unit) : PermissionRequestEvent
    class OnPermissionGranted(val onProceed: () -> Unit) : PermissionRequestEvent
    class OnGotoSettings(val onProceed: (Boolean) -> Unit) : PermissionRequestEvent
    object Idle : PermissionRequestEvent
}
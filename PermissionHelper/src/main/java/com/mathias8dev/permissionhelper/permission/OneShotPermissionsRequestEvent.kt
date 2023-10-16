package com.mathias8dev.permissionhelper.permission


internal sealed interface OneShotPermissionsRequestEvent {
    class OnShowRationale(
        val permissions: List<Permission>,
        val onProceed: (Boolean) -> Unit
    ) : OneShotPermissionsRequestEvent
    class OnRequestPermission(
        val permissions: List<Permission>,
        val onProceed: (Boolean) -> Unit
    ) : OneShotPermissionsRequestEvent
    class OnPermissionsGranted(
        val permissions: List<Permission>,
        val onProceed: () -> Unit
    ) : OneShotPermissionsRequestEvent
    class OnGotoSettings(
        val permissions: List<Permission>,
        val onProceed: (Boolean) -> Unit
    ) : OneShotPermissionsRequestEvent
    object Idle : OneShotPermissionsRequestEvent
}
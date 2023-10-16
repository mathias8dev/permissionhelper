package com.mathias8dev.permissionhelper.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


@Composable
fun OneShotPermissionsHelper(
    permissions: List<Permission>,
    permissionRequestUi: OneShotPermissionsRequestUi = OneShotPermissionsRequestUiImpl,
    content: @Composable OneShotPermissionsHelperScope.() -> Unit
) {

    val localContext = LocalContext.current

    val scope = remember(permissions, permissionRequestUi) {
        OneShotPermissionsHelperScopeImpl(
            permissions = permissions,
            context = localContext
        )
    }


    val permissionRequestEvent by scope.permissionRequestEvent.collectAsState()

    when(permissionRequestEvent) {
        is OneShotPermissionsRequestEvent.OnRequestPermission -> {
            val event = permissionRequestEvent as OneShotPermissionsRequestEvent.OnRequestPermission
            permissionRequestUi.OnPermissionsInitialRequest(
                permissions = event.permissions,
                onProceed =event.onProceed
            )
        }
        is OneShotPermissionsRequestEvent.OnPermissionsGranted -> {
            val event = permissionRequestEvent as OneShotPermissionsRequestEvent.OnPermissionsGranted
            permissionRequestUi.OnPermissionsGranted(
                permissions = event.permissions,
                onProceed = event.onProceed
            )
        }
        is OneShotPermissionsRequestEvent.OnShowRationale -> {
            val event = permissionRequestEvent as OneShotPermissionsRequestEvent.OnShowRationale
            permissionRequestUi.OnPermissionsShowRationale(
                permissions = event.permissions,
                onProceed = event.onProceed
            )
        }

        is OneShotPermissionsRequestEvent.OnGotoSettings -> {
            val event = permissionRequestEvent as OneShotPermissionsRequestEvent.OnGotoSettings
            permissionRequestUi.OnPermissionsGotoSettings(
                permissions = event.permissions,
                onProceed = event.onProceed
            )
        }
        else -> {}
    }

    content(scope)
}
package com.mathias8dev.permissionhelper.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun MultiplePermissionHelper(
    permissionsWithLaunchers: Map<Permission, PermissionRequestUi?>,
    content: @Composable MultiplePermissionHelperScope.()->Unit
) {

    val localContext = LocalContext.current

    val scope = remember(permissionsWithLaunchers) {
        MultiplePermissionHelperScopeImpl(
            permissions = permissionsWithLaunchers.keys,
            context = localContext
        )
    }


    val permissionRequestEvent by scope.permissionRequestEvent.collectAsStateWithLifecycle()

    when(permissionRequestEvent) {
        is PermissionRequestEvent.OnRequestPermission -> {
            val event = permissionRequestEvent as PermissionRequestEvent.OnRequestPermission
            val permissionRequestUi = permissionsWithLaunchers[event.permission] ?: PermissionRequestRequestUiImpl
            permissionRequestUi.OnPermissionInitialRequest(
                permission = event.permission,
                onProceed =event.onProceed
            )
        }
        is PermissionRequestEvent.OnShowRationale -> {
            val event = permissionRequestEvent as PermissionRequestEvent.OnShowRationale
            val permissionRequestUi = permissionsWithLaunchers[event.permission] ?: PermissionRequestRequestUiImpl
            permissionRequestUi.OnPermissionShowRationale(
                permission = event.permission,
                onProceed = event.onProceed
            )
        }

        is PermissionRequestEvent.OnGotoSettings -> {
            val event = permissionRequestEvent as PermissionRequestEvent.OnGotoSettings
            val permissionRequestUi = permissionsWithLaunchers[event.permission] ?: PermissionRequestRequestUiImpl
            permissionRequestUi.OnPermissionGotoSettings(
                permission = event.permission,
                onProceed = event.onProceed
            )
        }
        else -> {}
    }

    content(scope)
}
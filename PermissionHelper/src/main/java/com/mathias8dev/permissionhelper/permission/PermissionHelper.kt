package com.mathias8dev.permissionhelper.permission


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext


@Composable
fun PermissionHelper(
    permission: Permission,
    permissionRequestUi: PermissionRequestUi = PermissionRequestRequestUiImpl,
    content: @Composable PermissionHelperScope.()->Unit
) {

    val localContext = LocalContext.current

    val scope = remember(permission, permissionRequestUi) {
        PermissionHelperScopeImpl(
            permission = permission,
            context = localContext
        )
    }


    val permissionRequestEvent by scope.permissionRequestEvent.collectAsState()

    when(permissionRequestEvent) {
        is PermissionRequestEvent.OnRequestPermission -> {
            permissionRequestUi.OnPermissionInitialRequest(
                permission = permission,
                onProceed = (permissionRequestEvent as PermissionRequestEvent.OnRequestPermission)
                    .onProceed
            )
        }
        is PermissionRequestEvent.OnPermissionGranted -> {
            permissionRequestUi.OnPermissionGranted(
                permission = permission,
                onProceed = (permissionRequestEvent as PermissionRequestEvent.OnPermissionGranted)
                    .onProceed
            )
        }
        is PermissionRequestEvent.OnShowRationale -> {
            permissionRequestUi.OnPermissionShowRationale(
                permission = permission,
                onProceed = (permissionRequestEvent as PermissionRequestEvent.OnShowRationale)
                    .onProceed
            )
        }

        is PermissionRequestEvent.OnGotoSettings -> {
            permissionRequestUi.OnPermissionGotoSettings(
                permission = permission,
                onProceed = (permissionRequestEvent as PermissionRequestEvent.OnGotoSettings)
                    .onProceed
            )
        }
        else -> {}
    }

    content(scope)
}



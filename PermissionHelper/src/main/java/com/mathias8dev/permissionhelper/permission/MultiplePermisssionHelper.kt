package com.mathias8dev.permissionhelper.permission

import androidx.compose.runtime.Composable


@Composable
fun MultiplePermissionHelper(
    permissionsWithLaunchers: List<Pair<Permission, PermissionRequestUi>>,
    content: @Composable MultiplePermissionHelperScope.()->Unit
) {

}
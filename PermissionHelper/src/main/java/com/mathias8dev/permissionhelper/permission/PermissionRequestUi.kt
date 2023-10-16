package com.mathias8dev.permissionhelper.permission

import androidx.compose.runtime.Composable
import com.mathias8dev.permissionhelper.permission.Permission


interface PermissionRequestUi {

    /**
     * The Ui to show to the user when permission should be launched first time
     * [permission]: The current permission that needs to be granted
     * [onProceed]: Method to call mandatory when the user do the action the first time
     * */
    @Composable
    fun OnPermissionInitialRequest(
        permission: Permission,
        onProceed: (Boolean)->Unit,
    )

    /**
     * The Ui to show to the user when he needs to be convince before
     * [permission]: The current permission that needs to be granted
     * [onProceed]: Method to call mandatory when the user do the action that should make us ask
     * the permission again
     * */
    @Composable
    fun OnPermissionShowRationale(
        permission: Permission,
        onProceed: (Boolean)->Unit,
    )




    /**
     * The Ui to show to the user when he should go to settings to grant for the permission
     * [permission]: The current permission that needs to be granted
     * [onProceed]: Method to call mandatory when the user do the action that should navigate him to
     * the settings screen
     * */
    @Composable
    fun OnPermissionGotoSettings(
        permission: Permission,
        onProceed: (Boolean)->Unit,
    )
}
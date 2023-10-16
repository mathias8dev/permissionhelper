package com.mathias8dev.permissionhelper.permission

import androidx.compose.runtime.Composable

interface OneShotPermissionsRequestUi {

    /**
     * The Ui to show to the user when permission should be launched first time
     * [permissions]: The current permissions that needs to be granted
     * [onProceed]: Method to call mandatory when the user do the action the first time
     * */
    @Composable
    fun OnPermissionsInitialRequest(
        permissions: List<Permission>,
        onProceed: (Boolean)->Unit,
    )

    /**
     * The Ui to show to the user when he needs to be convince before
     * [permissions]: The current permissions that needs to be granted
     * [onProceed]: Method to call mandatory when the user do the action that should make us ask
     * the permission again
     * */
    @Composable
    fun OnPermissionsShowRationale(
        permissions: List<Permission>,
        onProceed: (Boolean)->Unit,
    )



    /**
     * The Ui to show to the user when he should go to settings to grant for the permission
     * [permissions]: The current permissions that needs to be granted
     * [onProceed]: Method to call mandatory when the user do the action that should navigate him to
     * the settings screen
     * */
    @Composable
    fun OnPermissionsGotoSettings(
        permissions: List<Permission>,
        onProceed: (Boolean)->Unit,
    )
}
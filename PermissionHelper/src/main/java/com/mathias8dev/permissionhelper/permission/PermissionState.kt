package com.mathias8dev.permissionhelper.permission

import androidx.compose.runtime.Stable


@Stable
sealed interface PermissionState {
    object Granted: PermissionState
    object Denied: PermissionState
    object ShowRationale: PermissionState
}

val PermissionState.isGranted: Boolean
    get() = this == PermissionState.Granted

val PermissionState.shouldShowRationale: Boolean
    get() = this == PermissionState.ShowRationale


val PermissionState.Denied: Boolean
    get() = this == PermissionState.Denied




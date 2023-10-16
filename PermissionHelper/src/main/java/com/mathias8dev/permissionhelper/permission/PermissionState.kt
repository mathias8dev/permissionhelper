package com.mathias8dev.permissionhelper.permission

import androidx.compose.runtime.Stable


@Stable
sealed interface PermissionState {
    object Granted: PermissionState
    object Denied: PermissionState
}

val PermissionState.isGranted: Boolean
    get() = this == PermissionState.Granted


val PermissionState.isDenied: Boolean
    get() = this == PermissionState.Denied




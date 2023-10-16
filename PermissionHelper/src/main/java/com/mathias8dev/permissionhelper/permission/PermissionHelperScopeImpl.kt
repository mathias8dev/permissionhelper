package com.mathias8dev.permissionhelper.permission

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



@Stable
internal class PermissionHelperScopeImpl internal constructor(
    permission: Permission,
    context: Context,
) : BasePermissionHelperScopeImpl(context) {


    init {
        currentLaunchedPermission = permission
    }

}
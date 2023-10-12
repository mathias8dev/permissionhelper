package com.mathias8dev.permissionhelper.permission

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


object PermissionRequestRequestUiImpl : PermissionRequestUi {

    @Composable
    override fun OnPermissionInitialRequest(permission: Permission, onProceed: (Boolean) -> Unit) {
        PermissionDialog(
            makeContentScrollable = true,
            onDismissRequest = { onProceed(false) }
        ) {
            permission.iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "Permission illustration icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = permission.titleRes?.let { stringResource(id = it) }
                    ?: "Enable ${permission.manifestName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = permission.descriptionRes?.let { stringResource(id = it) }
                    ?: "Please we need this permission to make you use all features of this application"
            )
            TextButton(
                onClick = {
                    onProceed(true)
                },
                content = {
                    Text(text = "OK")
                }
            )
        }
    }

    @Composable
    override fun OnPermissionShowRationale(permission: Permission, onProceed: (Boolean) -> Unit) {
        PermissionDialog(
            makeContentScrollable = true,
            onDismissRequest = { onProceed(false) }
        ) {
            permission.iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "Permission illustration icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = permission.titleRes?.let { stringResource(id = it) }
                    ?: "Enable ${permission.manifestName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = permission.rationaleRes?.let { stringResource(id = it) }
                    ?: "Please we need this permission to make you use all features of this application"
            )
            TextButton(
                onClick = {
                    onProceed(true)
                },
                content = {
                    Text(text = "OK")
                }
            )
        }
    }

    @Composable
    override fun OnPermissionGranted(permission: Permission, onProceed: () -> Unit) {
        PermissionDialog(
            makeContentScrollable = true,
            onDismissRequest = { onProceed() }
        ) {
            permission.iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "Permission illustration icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = permission.titleRes?.let { stringResource(id = it) }
                    ?: "Enable ${permission.manifestName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = permission.grantedRes?.let { stringResource(id = it) }
                    ?: "Thank you for granting the permission"
            )
            TextButton(
                onClick = {
                    onProceed()
                },
                content = {
                    Text(text = "OK")
                }
            )
        }
    }

    @Composable
    override fun OnPermissionGotoSettings(permission: Permission, onProceed: (Boolean) -> Unit) {
        PermissionDialog(
            makeContentScrollable = true,
            onDismissRequest = { onProceed(false) }
        ) {
            permission.iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "Permission illustration icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = permission.titleRes?.let { stringResource(id = it) }
                    ?: "Enable ${permission.manifestName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = permission.gotoSettingsRes?.let { stringResource(id = it) }
                    ?: "We are not authorized to ask you for the permission. Please goto settings to do it yourself"
            )
            TextButton(
                onClick = {
                    onProceed(true)
                },
                content = {
                    Text(text = "Goto settings")
                }
            )
        }
    }
}
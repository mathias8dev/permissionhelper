package com.mathias8dev.permissionhelper.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


object OneShotPermissionsRequestUiImpl : OneShotPermissionsRequestUi {


    @Composable
    private fun ListItem(
        content: @Composable () -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            content()
        }
    }


    @Composable
    override fun OnPermissionsInitialRequest(
        permissions: List<Permission>,
        onProceed: (Boolean) -> Unit
    ) {
        val icons = remember {
            permissions.mapNotNull { it.iconRes }
        }
        PermissionDialog(
            makeContentScrollable = true,
            onDismissRequest = { onProceed(false) }
        ) {
            if (icons.isNotEmpty()) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                icons.map {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "Permission illustration icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (icons.isNotEmpty()) Spacer(modifier = Modifier.height(32.dp))

            permissions.forEach { permission ->
                ListItem {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = permission.titleRes?.let { stringResource(id = it) }
                            ?: "Enable ${permission.manifestName}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = permission.descriptionRes?.let { stringResource(id = it) }
                        ?: "Please we need this permission to make you use all features of this application"
                )
            }


            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
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
    override fun OnPermissionsShowRationale(
        permissions: List<Permission>,
        onProceed: (Boolean) -> Unit
    ) {
        val icons = remember {
            permissions.mapNotNull { it.iconRes }
        }
        PermissionDialog(
            makeContentScrollable = true,
            onDismissRequest = { onProceed(false) }
        ) {
            if (icons.isNotEmpty()) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                icons.map {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "Permission illustration icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (icons.isNotEmpty()) Spacer(modifier = Modifier.height(32.dp))

            permissions.forEach { permission ->
                ListItem {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = permission.titleRes?.let { stringResource(id = it) }
                            ?: "Enable ${permission.manifestName}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = permission.rationaleRes?.let { stringResource(id = it) }
                        ?: "Please we need this permission to make you use all features of this application"
                )
            }
            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
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
    override fun OnPermissionsGranted(permissions: List<Permission>, onProceed: () -> Unit) {
        val icons = remember {
            permissions.mapNotNull { it.iconRes }
        }
        PermissionDialog(
            makeContentScrollable = true,
            onDismissRequest = { onProceed() }
        ) {
            if (icons.isNotEmpty()) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                icons.map {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "Permission illustration icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (icons.isNotEmpty()) Spacer(modifier = Modifier.height(32.dp))

            permissions.forEach { permission ->
                ListItem {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = permission.grantedRes?.let { stringResource(id = it) }
                            ?: "Thank you for granting the permission"
                    )
                }
            }
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
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
    override fun OnPermissionsGotoSettings(
        permissions: List<Permission>,
        onProceed: (Boolean) -> Unit
    ) {
        val icons = remember {
            permissions.mapNotNull { it.iconRes }
        }
        PermissionDialog(
            makeContentScrollable = true,
            onDismissRequest = { onProceed(false) }
        ) {
            if (icons.isNotEmpty()) Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                icons.map {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "Permission illustration icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (icons.isNotEmpty()) Spacer(modifier = Modifier.height(32.dp))
            permissions.forEach { permission ->
                ListItem {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = permission.titleRes?.let { stringResource(id = it) }
                            ?: "Enable ${permission.manifestName}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = permission.gotoSettingsRes?.let { stringResource(id = it) }
                        ?: "We are not authorized to ask you for the permission. Please goto settings to do it yourself"
                )
            }

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
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
package com.mathias8dev.sample

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mathias8dev.permissionhelper.permission.MultiplePermissionHelper
import com.mathias8dev.permissionhelper.permission.OneShotPermissionsHelper
import com.mathias8dev.permissionhelper.permission.Permission
import com.mathias8dev.permissionhelper.permission.PermissionHelper
import com.mathias8dev.permissionhelper.permission.isGranted
import com.mathias8dev.sample.ui.theme.SampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                val localContext = LocalContext.current
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val cameraPermission = remember {
                        Permission(
                            manifestKey = Manifest.permission.CAMERA
                        )
                    }

                    val coarseLocation = remember {
                        Permission(
                            manifestKey = Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    }

                    val fineLocation = remember {
                        Permission(
                            manifestKey = Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PermissionHelper(
                            permission = cameraPermission
                        ) {
                            TextButton(onClick = {
                                launchPermission {
                                    Toast.makeText(
                                        localContext,
                                        if (it.isGranted) "Camera permission enable successfully"
                                        else "The camera permission is not enable",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }) {
                                Text("Test single permission")
                            }
                        }

                        MultiplePermissionHelper(
                            permissionsWithLaunchers = mapOf(
                                fineLocation to null,
                                coarseLocation to null,
                                cameraPermission to null
                            )
                        ) {
                            TextButton(onClick = {
                                launchPermissions {
                                    Toast.makeText(
                                        localContext,
                                        if (it.all { itt -> itt.second.isGranted }) "permissions granted"
                                        else "permissions are not granted",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }) {
                                Text("Test multiple permissions")
                            }

                        }


                        OneShotPermissionsHelper(
                            permissions = listOf(fineLocation, coarseLocation, cameraPermission)
                        ) {
                            TextButton(onClick = {
                                launchPermissions {
                                    Toast.makeText(
                                        localContext,
                                        if (it.all { itt -> itt.second.isGranted }) "permissions granted"
                                        else "permissions are not granted",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }) {
                                Text("Test one shot multiple permissions")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleTheme {
        Greeting("Android")
    }
}
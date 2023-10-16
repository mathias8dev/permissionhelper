# PermissionHelper Android Library
Simple Android library to help to manage android permission request flow.

[![Jitpack latest version](https://jitpack.io/v/mathias8dev/permissionhelper.svg)](https://jitpack.io/#mathias8dev/permissionhelper)


## Setup
### 1. Import JitPack Android Library
Add `maven { url 'https://jitpack.io' }` in
<details open>
  <summary>groovy - settings.gradle</summary>

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven { url 'https://jitpack.io' }
    }
}
```
</details>

<details open>
  <summary>kotlin - settings.gradle.kts</summary>

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven ("https://jitpack.io")
    }
}
```
</details>

### 2. Add dependency
<details open>
  <summary>groovy - build.gradle</summary>

```gradle
dependencies {
    implementation "com.github.mathias8dev:permissionhelper:latest-version"
}
```
</details>
<details open>
  <summary>kotlin - build.gradle.kts</summary>

```gradle
dependencies {
    implementation("com.github.mathias8dev:permissionhelper:latest-version")
}
```
</details>

## Usage
### Import
```kotlin
import com.github.mathias8dev.permissionhelper.permission.PermissionHelper
```
or
```kotlin
import com.github.mathias8dev.permissionhelper.permission.MultiplePermissionHelper
```

or

```kotlin
import com.github.mathias8dev.permissionhelper.permission.OneShotPermissionsHelper
```

### Examples
 <summary>Using Single Permission request with PermissionHelper</summary>

```kotlin
val cameraPermission = remember {  
  Permission(  
        manifestKey = Manifest.permission.CAMERA  
  )  
}

PermissionHelper(  
    permission = cameraPermission  
) {  
  Column(  
        modifier = Modifier  
            .fillMaxSize()  
            .padding(16.dp),  
	    verticalArrangement = Arrangement.Center,  
	    horizontalAlignment = Alignment.CenterHorizontally  
  ) {  
  
	  TextButton(
		  onClick = {  
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
}
```

<summary>Using Multiple Permission request with MultiplePermissionHelper</summary>

```kotlin
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

MultiplePermissionHelper(  
    permissionsWithLaunchers = mapOf(  
        fineLocation to null,  
	    coarseLocation to null,  
	    cameraPermission to null  
   )  
) {  
  Column(  
        modifier = Modifier  
            .fillMaxSize()  
            .padding(16.dp),  
	    verticalArrangement = Arrangement.Center,  
	    horizontalAlignment = Alignment.CenterHorizontally  
  ) {  
  
  // You may want to provide an optional launch strategy
  launchStrategy {  
	  PermissionLaunchStrategy.Builder()  
	    .forPermission(fineLocation)  
	    .abortOnFail(false)  
	    .and()  
	    .forPermission(coarseLocation)  
	    .delayForNextRequest(2.seconds)  
	    .abortOnFail(false)  
	    .skipStrategyIfAlreadyGranted(true)  
	    .and()  
	    .build()  
  }  
  
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
 }}
```

<summary>Using One Shot Permission request with OneShotPermissionsHelper</summary>

```kotlin
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

OneShotPermissionsHelper(
    permissions = listOf(fineLocation, coarseLocation, cameraPermission)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextButton(
            onClick = {
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
}
```

## License
```
Copyright 2023 Kossi Mathias KALIPE

Licensed under the Apache License, Version 2.0 (the "License");

you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
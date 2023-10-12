package com.mathias8dev.permissionhelper.permission

data class Permission(
    val manifestKey: String,
    val iconRes: Int? = null,
    val lottieRes: Int? = null,
    val titleRes: Int? = null,
    val descriptionRes: Int? = null,
    val rationaleRes: Int? = null,
    val deniedRes: Int? = null,
    val grantedRes: Int? = null,
    val gotoSettingsRes: Int? = null,
    val lottieResIsPriority: Boolean = false,
)


val Permission.manifestName: String
    get() = this.manifestKey.split(".").last()
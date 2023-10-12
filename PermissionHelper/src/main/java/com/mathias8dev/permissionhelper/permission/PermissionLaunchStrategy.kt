package com.mathias8dev.permissionhelper.permission

import kotlin.time.Duration

class PermissionLaunchStrategy internal constructor(
    val chainMap: Map<Permission, PermissionConfig>
) {

    class Builder : ChainBuilder {
        private val chainMap = mutableMapOf<Permission, PermissionConfig>()
        private var currentPermission: Permission? = null


        override fun forPermission(permission: Permission): LaunchFlowBuilder {
            currentPermission = permission
            return LaunchFlowBuilderImpl(this)
        }

        override fun chain(config: PermissionConfig) {
            if (currentPermission == null) throw IllegalStateException(
                "Since there is no way to create an instance of a PermissionConfig, this method" +
                        " could not be called if the currentPermission point to a null object"
            )
            currentPermission?.let {
                chainMap[it.copy()] = config
            }
            currentPermission = null
        }

        override fun build(): PermissionLaunchStrategy {

            return PermissionLaunchStrategy(
                chainMap
            )
        }

    }
}

class PermissionConfig internal constructor(
    val permissionsToCheck: List<Permission> = emptyList(),
    val delayForNextRequest: Duration? = null,
    val suspendedCall: (suspend ()->Unit)? = null
)


interface ChainBuilder {
    fun forPermission(permission: Permission): LaunchFlowBuilder
    fun chain(config: PermissionConfig)
    fun build(): PermissionLaunchStrategy
}

interface LaunchFlowBuilder {
    fun checkHasPermissions(vararg permission: Permission): LaunchFlowBuilder
    fun delayForNextRequest(duration: Duration?): LaunchFlowBuilder
    fun makeSuspendedCallBeforeNext(call: suspend ()->Unit, wait: Boolean): LaunchFlowBuilder
    fun and(): ChainBuilder
}

internal class LaunchFlowBuilderImpl internal constructor(
    private val chainBuilder: ChainBuilder
) : LaunchFlowBuilder {

    private val permissionsToCheck: MutableSet<Permission> = mutableSetOf()
    private var delayForNextRequest: Duration? = null
    private var suspendedCall: (suspend ()->Unit)? = null

    override fun checkHasPermissions(vararg permission: Permission): LaunchFlowBuilder {
        permissionsToCheck.addAll(permission)
        return this
    }

    override fun delayForNextRequest(duration: Duration?): LaunchFlowBuilder {
        this.delayForNextRequest = duration
        return this
    }

    override fun makeSuspendedCallBeforeNext(
        call: suspend () -> Unit,
        wait: Boolean
    ): LaunchFlowBuilder {
        this.suspendedCall = call
        return this
    }

    override fun and(): ChainBuilder {
        return chainBuilder.apply {
            this.chain(
                PermissionConfig(
                    permissionsToCheck.toList(),
                    delayForNextRequest,
                    suspendedCall
                )
            )
        }
    }

}



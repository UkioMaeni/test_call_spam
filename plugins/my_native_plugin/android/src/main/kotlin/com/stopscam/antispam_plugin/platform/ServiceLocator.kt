package com.stopscam.antispam_plugin.platform

import android.app.Activity
import com.stopscam.antispam_plugin.domain.gateway.CallScreenGateway
import com.stopscam.antispam_plugin.platform.gateway.CallScreenGatewayImpl
import android.content.Context

object ServiceLocator {
    private lateinit var appContext: Context
    public var activity: Activity? = null
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Infra-слой: реализация интерфейсов домена
    val callScreenRoleGateway: CallScreenGateway by lazy {
        CallScreenGatewayImpl(appContext)
    }
}

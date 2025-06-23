package com.stopscam.antispam_plugin.platform

import android.app.Activity
import com.stopscam.antispam_plugin.domain.gateway.CallScreenGateway
import com.stopscam.antispam_plugin.platform.gateway.CallScreenGatewayImpl
import android.content.Context
import com.stopscam.antispam_plugin.data.local.db.AppDatabase
import com.stopscam.antispam_plugin.data.local.prefs.SpamPrefs
import com.stopscam.antispam_plugin.domain.gateway.DBUpdaterGateway
import com.stopscam.antispam_plugin.domain.gateway.PhoneLogGateway
import com.stopscam.antispam_plugin.platform.gateway.DBUpdaterGatewayImpl
import com.stopscam.antispam_plugin.platform.gateway.PhoneLogGatewayImpl

object ServiceLocator {
    private lateinit var appContext: Context

    public var activity: Activity? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(appContext)
    }

    val prefs = SpamPrefs(appContext)

    val callScreenRoleGateway: CallScreenGateway by lazy {
        CallScreenGatewayImpl(appContext)
    }

    val dbUpdaterGateway: DBUpdaterGateway by lazy {
        DBUpdaterGatewayImpl(appContext)
    }

    val phoneLogGateway: PhoneLogGateway by lazy {
        PhoneLogGatewayImpl(appContext)
    }

}

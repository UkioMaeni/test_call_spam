package com.stopscam.antispam_plugin.platform.handlers.allow_number

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context
import com.stopscam.antispam_plugin.platform.handlers.common.CallMethods
import com.stopscam.antispam_plugin.platform.handlers.common.Handler

class AllowNumberInsertHandler : Handler {


    override val callMethod : String = CallMethods.ALLOW_NUMBER_INSERT;

    override fun handler(context: Context, call: MethodCall, result: MethodChannel.Result){
        val meta :LocationServiceMeta = LocationService.getMeta(context)
        val map = mapOf(
            "tickerSeconds" to meta.tickerSeconds,
            "tickersCount"   to meta.tickersCount,
            "hash"          to meta.hash,
            "orderId"       to meta.orderId
        )
        result.success(map)
    }
}
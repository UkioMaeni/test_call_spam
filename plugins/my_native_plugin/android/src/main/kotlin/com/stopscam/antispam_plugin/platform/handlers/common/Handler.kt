package com.stopscam.antispam_plugin.platform.handlers.common

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import android.content.Context

interface Handler {
    val callMethod:String;
    fun handler(context: Context, call: MethodCall, result: MethodChannel.Result);
}


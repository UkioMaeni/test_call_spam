package com.stopscam.antispam_plugin.domain.gateway

import android.app.Activity
import android.content.Context

interface CallScreenGateway{
    fun isGranted(): Boolean
    suspend fun requestRole():Boolean
    fun handleActivityResult(resultCode: Int)
}
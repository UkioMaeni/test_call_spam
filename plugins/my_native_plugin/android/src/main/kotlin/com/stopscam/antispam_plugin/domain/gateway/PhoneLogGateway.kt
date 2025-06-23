package com.stopscam.antispam_plugin.domain.gateway

interface PhoneLogGateway{
    suspend fun getCallsLog(): Any;
    suspend fun getSMSLog():Any
}


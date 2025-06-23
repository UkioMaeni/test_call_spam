package com.stopscam.antispam_plugin.domain.gateway

interface DBUpdaterGateway{
    suspend fun isRunning(): Boolean;
    suspend fun updateDb():Boolean
}


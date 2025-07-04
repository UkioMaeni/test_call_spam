package com.stopscam.antispam_plugin.platform.handlers.common

object CallMethods {

    //AllowNumber
    const val ALLOW_NUMBER_INSERT  = "ALLOW_NUMBER_INSERT"
    const val ALLOW_NUMBER_DELETE  = "ALLOW_NUMBER_DELETE"
    const val ALLOW_NUMBER_GET_ALL = "ALLOW_NUMBER_GET_ALL"
    //CustomNumber
    const val CUSTOM_NUMBER_INSERT  = "CUSTOM_NUMBER_INSERT"
    const val CUSTOM_NUMBER_DELETE  = "CUSTOM_NUMBER_DELETE"
    const val CUSTOM_NUMBER_GET_ALL = "CUSTOM_NUMBER_GET_ALL"
    //CallScreeenRole
    const val CALL_SCREEN_ROLE_STATUS  = "CALL_SCREEN_ROLE_STATUS"
    const val CALL_SCREEN_ROLE_REQUEST = "CALL_SCREEN_ROLE_REQUEST"
    //CallBlock
    const val CALL_BLOCK_STATUS     = "CALL_BLOCK_STATUS"
    const val CALL_BLOCK_SET_STATUS = "CALL_BLOCK_SET_STATUS"
     //UpdateDb
    const val UPDATE_DB_STATUS = "UPDATE_DB_STATUS"
    const val UPDATE_DB_START  = "UPDATE_DB_START"

    const val PHONE_LOG_CALLS = "PHONE_LOG_CALLS"
    const val PHONE_LOG_SMS  = "PHONE_LOG_SMS"

    const val DESCRIPTION_SCAM = "DESCRIPTION_SCAM"
}
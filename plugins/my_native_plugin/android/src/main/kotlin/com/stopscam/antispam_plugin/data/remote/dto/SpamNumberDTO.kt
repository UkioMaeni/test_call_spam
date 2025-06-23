package com.stopscam.antispam_plugin.data.remote.dto

import androidx.annotation.Keep
import com.stopscam.antispam_plugin.data.local.entity.SpamNumber

@Keep
data class SpamNumberDTO(
    val id: Int           = 0,         
    val number: String    = "",
    val description: String = ""
) {
    fun toEntity() = SpamNumber(
        serverId    = id,
        number      = number,
        description = description
    )
}
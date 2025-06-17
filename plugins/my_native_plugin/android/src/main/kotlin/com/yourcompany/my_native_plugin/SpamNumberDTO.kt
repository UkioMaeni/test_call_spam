package com.yourcompany.my_native_plugin

import androidx.annotation.Keep

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
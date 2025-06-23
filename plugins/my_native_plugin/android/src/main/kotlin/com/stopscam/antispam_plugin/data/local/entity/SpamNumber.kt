package com.stopscam.antispam_plugin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.Keep

@Keep
@Entity
data class SpamNumber(
    @PrimaryKey val number: String,
    val description: String,
    val serverId: Int,
)
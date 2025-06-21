package com.stopscam.antispam_plugin.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.Keep

@Keep
@Entity
data class SpamCustomNumber(
    @PrimaryKey val number: String,
    val description: String,
    val setBlocked: Boolean = true
)
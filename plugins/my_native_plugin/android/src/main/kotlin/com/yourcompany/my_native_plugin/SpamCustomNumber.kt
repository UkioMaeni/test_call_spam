package com.yourcompany.my_native_plugin

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SpamCustomNumber(
    @PrimaryKey val number: String,
    val description: String
)
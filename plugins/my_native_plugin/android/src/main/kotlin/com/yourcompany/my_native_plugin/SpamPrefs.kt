package com.yourcompany.my_native_plugin

import android.content.Context

private const val PREFS_NAME = "spam_prefs"
private const val KEY_BLOCK_ENABLED = "block_enabled"

object SpamPrefs {

    fun isBlockingEnabled(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
           .getBoolean(KEY_BLOCK_ENABLED, false)          // по умолчанию ВКЛ

    fun setBlockingEnabled(ctx: Context, flag: Boolean) =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
           .edit()
           .putBoolean(KEY_BLOCK_ENABLED, flag)
           .apply()
}
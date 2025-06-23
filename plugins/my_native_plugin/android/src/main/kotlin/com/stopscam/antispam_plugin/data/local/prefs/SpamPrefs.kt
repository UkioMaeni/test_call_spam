package com.stopscam.antispam_plugin.data.local.prefs

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

private const val PREFS_NAME = "spam_prefs"
private const val KEY_BLOCK_ENABLED = "block_enabled"

class SpamPrefs(
    private  val ctx: Context
) {

    fun isBlockingEnabled(): Boolean =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
           .getBoolean(KEY_BLOCK_ENABLED, false)

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    fun setBlockingEnabled(flag: Boolean) =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
           .edit()
           .putBoolean(KEY_BLOCK_ENABLED, flag)
           .apply()
}
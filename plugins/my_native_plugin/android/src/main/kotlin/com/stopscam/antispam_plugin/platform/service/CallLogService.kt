package com.yourcompany.my_native_plugin

import android.net.Uri
import android.content.Context
import android.os.Build
import android.provider.CallLog
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

/**
 * Единственный объект-сервис, через который приложение читает журнал звонков.
 *
 * ─ Требуется разрешение READ_CALL_LOG (+ роль Dialer на Android 9+).
 * ─ Данные возвращаются как список Map, готовый для передачи в MethodChannel.
 */
@Keep
object CallLogService {

    /**  
     * Возвращает [limit] последних записей журнала, отсортированных по дате ↓.
     *
     * @param context  `applicationContext` или любой Context.
     * @param limit    сколько строк нужно (по-умолчанию – 50).
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresPermission(android.Manifest.permission.READ_CALL_LOG)
    fun getCallLog(context: Context, limit: Int = 50): List<Map<String, Any?>> {
        val projection = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )
        val uri: Uri = CallLog.Calls.CONTENT_URI
            .buildUpon()
            .appendQueryParameter("limit", limit.toString())
            .build()
        val sortOrder = "${CallLog.Calls.DATE} DESC"
        val result = mutableListOf<Map<String, Any?>>()

        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idxNum  = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val idxType = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val idxDate = cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)
            val idxDur  = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)

            while (cursor.moveToNext()) {
                result += mapOf(
                    "number"    to cursor.getString(idxNum),
                    "type"      to cursor.getInt(idxType),   // 1=in, 2=out, 3=missed, 5=rejected
                    "timestamp" to cursor.getLong(idxDate),
                    "duration"  to cursor.getInt(idxDur)
                )
            }
        }
        return result
    }
}

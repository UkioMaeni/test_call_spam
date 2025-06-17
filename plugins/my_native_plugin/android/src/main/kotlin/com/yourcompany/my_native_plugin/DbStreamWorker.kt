package com.yourcompany.my_native_plugin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/* IO / Net */
import java.util.zip.GZIPInputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody

/* JSON */
import com.google.gson.Gson

import android.app.Service
import androidx.core.app.ServiceCompat
import android.content.pm.ServiceInfo
class DbStreamWorker(appContext: Context, params: WorkerParameters)
    : CoroutineWorker(appContext, params) {

    private val dao = AppDatabase.getInstance(appContext).spamDao()
    private val NOTIF_ID = 42
    private val CHANNEL  = "spam_update"

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // 1. делаем foreground-уведомление
        createChannel()
        setForeground(createNotif(0, 0))

        try {
            val url = "https://call.stopscam.ai/api/v1/numberRouter/load_db_file"
            val req = Request.Builder().url(url).header("Accept-Encoding","gzip").build()
            val resp = OkHttpClient().newCall(req).execute()

            if (!resp.isSuccessful) return@withContext Result.retry()

            val reader = GZIPInputStream(resp.body!!.byteStream())
                .bufferedReader(Charsets.UTF_8)

            val gson  = Gson()
            val batch = mutableListOf<SpamNumber>()
            val BATCH = 10_000
            var count = 0

            while (true) {
                val line = reader.readLine() ?: break          // ← блокирующее чтение
                val dto  = gson.fromJson(line, SpamNumberDTO::class.java)
                batch.add(dto.toEntity())

                if (batch.size >= BATCH) {
                    dao.insertAll(batch)                       // теперь можно
                    count += batch.size
                    updateNotif(count)
                    batch.clear()
                }
            }
            if (batch.isNotEmpty()) {
                dao.insertAll(batch)
                count += batch.size
                updateNotif(count)

            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    /* ---------- helpers ---------- */

    private fun createChannel() {
        val mgr = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel(
                CHANNEL, "Spam-база", NotificationManager.IMPORTANCE_LOW)
            mgr.createNotificationChannel(ch)
        }
    }

    private fun createNotif(done: Int, max: Int): ForegroundInfo {
                val notif = NotificationCompat.Builder(applicationContext, CHANNEL)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Обновление спам-базы")
                .setContentText(
                    if (max == 0) "Начинаем загрузку…" else "$done / $max записей"
                )
                .setProgress(max, done, max == 0)          // indeterminate, пока max == 0
                .setOngoing(true)                          // нельзя свайпнуть
                .build()

            /* 2. Тип foreground-сервиса: начиная с API 34 обязателен */
            return if (Build.VERSION.SDK_INT >= 34) {
                ForegroundInfo(
                    NOTIF_ID,
                    notif,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC   // ← константа из core
                )
            } else {
                // до Android 13 включительно тип передавать нельзя
                ForegroundInfo(NOTIF_ID, notif)
            } 
    }

    private suspend fun updateNotif(done: Int, doneText: Boolean = false) {
        val mgr = NotificationCompat.Builder(applicationContext, CHANNEL)
            .setSmallIcon(
              if (doneText) android.R.drawable.stat_sys_download_done
              else android.R.drawable.stat_sys_download)
            .setContentTitle(
              if (doneText) "База обновлена" else "Обновление спам-базы")
            .setContentText(
              if (doneText) "Загружено $done номеров"
              else "Загружено $done…")
            .setProgress(0, 0, !doneText)
            .setOngoing(!doneText)
            .build()
        withContext(Dispatchers.Main) {
            NotificationManagerCompat.from(applicationContext)
                .notify(NOTIF_ID, mgr)
        }
    }
}

package com.stopscam.antispam_plugin.platform.gateway

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.stopscam.antispam_plugin.domain.gateway.DBUpdaterGateway
import com.stopscam.antispam_plugin.platform.service.DbStreamWorker

class DBUpdaterGatewayImpl(
    private val ctx: Context,
): DBUpdaterGateway {
    override suspend fun isRunning(): Boolean {
        val list = WorkManager.getInstance(ctx)
            .getWorkInfosForUniqueWork("spam-db-update")   // или .getWorkInfosByTag(TAG)
            .await()
        val isRunning = list.any { it.state == WorkInfo.State.RUNNING }
        return  isRunning;
    }

    override suspend fun updateDb():Boolean {
        val req = OneTimeWorkRequestBuilder<DbStreamWorker>()
            .addTag("db_stream_worker")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(ctx)
            .enqueueUniqueWork("spam-db-update",
                ExistingWorkPolicy.REPLACE, req)
        return  true
    }

}
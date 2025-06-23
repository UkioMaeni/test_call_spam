package com.stopscam.antispam_plugin.platform.gateway

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.stopscam.antispam_plugin.domain.gateway.CallScreenGateway
import com.stopscam.antispam_plugin.platform.ServiceLocator
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class CallScreenGatewayImpl(
    private val ctx: Context,
) : CallScreenGateway {

    private var continuation: Continuation<Boolean>? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun isGranted(): Boolean{

        val rm = ctx.getSystemService(RoleManager::class.java)

        val held = rm.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
                rm.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        return held
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun requestRole():Boolean = suspendCancellableCoroutine { cont ->
        if(isGranted()){
            cont.resume(true)
            return@suspendCancellableCoroutine
        }
        val act = ServiceLocator.activity;
        if (act == null) {
            cont.resume(false); return@suspendCancellableCoroutine
        }
        val rm = act.getSystemService(RoleManager::class.java)
        if (rm == null || !rm.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            cont.resume(false); return@suspendCancellableCoroutine
        }
        continuation = cont


        val intent = rm.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        act.startActivityForResult(intent, REQUEST_CODE_ROLE)

        cont.invokeOnCancellation { continuation = null }
    }

    companion object {
        const val REQUEST_CODE_ROLE = 321
    }

    override fun handleActivityResult(resultCode: Int) {
        val granted = (resultCode == Activity.RESULT_OK)
        continuation?.resume(granted)
        continuation = null
    }
}
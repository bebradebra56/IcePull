package com.icepull.app.rgerpofk.data.utils

import android.util.Log
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class IcePullPushToken {

    suspend fun icePullGetToken(
        icePullMaxAttempts: Int = 7,
        icePullDelayMs: Long = 1500
    ): String {

        repeat(icePullMaxAttempts - 1) {
            try {
                val icePullToken = FirebaseMessaging.getInstance().token.await()
                return icePullToken
            } catch (e: Exception) {
                Log.e(IcePullApplication.ICE_PULL_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(icePullDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(IcePullApplication.ICE_PULL_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}
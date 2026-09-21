package com.example.shinhan_qna_aos

import android.content.Context
import com.example.shinhan_qna_aos.API.APIInterface
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class FcmTokenRequest(val fcmToken: String)

class PushTokenRegistrar(private val context: Context, private val api: APIInterface, private val data: Data) {
    private var lastRegistered: Pair<String, String>? = null

    suspend fun syncCurrentToken() {
        if (data.accessToken.isNullOrBlank() || FirebaseApp.getApps(context).isEmpty()) return
        val token = runCatching {
            suspendCancellableCoroutine<String?> { continuation ->
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (continuation.isActive) continuation.resume(if (task.isSuccessful) task.result else null)
                }
            }
        }.getOrNull() ?: return
        registerToken(token)
    }

    suspend fun registerToken(token: String) {
        val accessToken = data.accessToken ?: return
        if (token.isBlank() || lastRegistered == (accessToken to token)) return
        runCatching {
            api.registerFcmToken("Bearer $accessToken", FcmTokenRequest(token))
        }.getOrNull()?.takeIf { it.isSuccessful }?.let { lastRegistered = accessToken to token }
    }
}

fun revokeDevicePushToken(context: Context) {
    if (FirebaseApp.getApps(context).isNotEmpty()) FirebaseMessaging.getInstance().deleteToken()
}

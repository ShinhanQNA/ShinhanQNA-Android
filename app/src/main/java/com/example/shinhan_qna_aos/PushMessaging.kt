package com.example.shinhan_qna_aos

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.successOrThrow
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class FcmTokenRequest(val fcmToken: String)

data class NotificationLaunch(val key: String, val route: String?)

internal data class IncomingPush(
    val title: String,
    val body: String,
    val type: String?,
    val targetId: String?,
    val key: String?
)

internal fun incomingPush(values: Map<String, String>): IncomingPush? {
    if (values.keys.none { it in setOf("title", "body", "type", "postId", "notification_key", "notification_type", "google.message_id", "gcm.notification.title", "gcm.n.title") }) return null
    return IncomingPush(
        title = values["title"] ?: values["gcm.notification.title"] ?: values["gcm.n.title"] ?: "신한 QnA",
        body = values["body"] ?: values["gcm.notification.body"] ?: values["gcm.n.body"] ?: "새로운 알림이 있습니다.",
        type = values["type"] ?: values["notification_type"] ?: values["gcm.notification.type"],
        targetId = values["postId"] ?: values["id"] ?: values["notification_id"] ?: values["gcm.notification.postId"],
        key = values["notification_key"] ?: values["google.message_id"]
    )
}

class PushTokenRegistrar(private val context: Context, private val api: APIInterface, private val data: Data) {
    private var lastRegistered: Pair<String, String>? = null

    suspend fun syncCurrentToken() {
        if (data.accessToken.isNullOrBlank() || FirebaseApp.getApps(context).isEmpty()) return
        val token = apiResult {
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
        apiResult {
            api.registerFcmToken(bearerHeader(accessToken), FcmTokenRequest(token)).successOrThrow()
        }.onSuccess { lastRegistered = accessToken to token }
    }
}

private const val PUSH_CHANNEL_ID = "shinhan_qna_channel"

fun revokeDevicePushToken(context: Context) {
    if (FirebaseApp.getApps(context).isNotEmpty()) FirebaseMessaging.getInstance().deleteToken()
}

fun createPushChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val channel = NotificationChannel(PUSH_CHANNEL_ID, "신한 QnA 알림", NotificationManager.IMPORTANCE_DEFAULT)
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
}

class QnaFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        val data = Data(this)
        if (data.accessToken.isNullOrBlank()) return
        CoroutineScope(Dispatchers.IO).launch {
            PushTokenRegistrar(this@QnaFirebaseMessagingService, com.example.shinhan_qna_aos.API.APIRetrofit.apiService, data)
                .registerToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (Data(this).accessToken.isNullOrBlank()) return
        val values = message.data.toMutableMap()
        message.notification?.title?.let { values["title"] = it }
        message.notification?.body?.let { values["body"] = it }
        message.messageId?.let { values["google.message_id"] = it }
        val incoming = incomingPush(values) ?: return
        val saved = PushNotificationStore.add(this, incoming.title, incoming.body, incoming.type, incoming.targetId, incoming.key)
        showPush(saved)
    }

    private fun showPush(notification: PushNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notification_key", notification.key)
            putExtra("title", notification.title)
            putExtra("body", notification.body)
            notification.type?.let { putExtra("type", it) }
            notification.targetId?.let { putExtra("postId", it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, notification.key.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val androidNotification = NotificationCompat.Builder(this, PUSH_CHANNEL_ID)
            .setSmallIcon(R.drawable.biglogo)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        runCatching { NotificationManagerCompat.from(this).notify(notification.key.hashCode(), androidNotification) }
    }
}

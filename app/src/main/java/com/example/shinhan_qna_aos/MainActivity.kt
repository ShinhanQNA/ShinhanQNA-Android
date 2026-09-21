package com.example.shinhan_qna_aos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.shinhan_qna_aos.API.APIRetrofit
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private var notificationLaunch by mutableStateOf<NotificationLaunch?>(null)
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiInterface = APIRetrofit.apiService
        notificationLaunch = readNotificationLaunch(intent)
        if (FirebaseApp.getApps(this).isNotEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        // 4. Compose 시작
        setContent {
            AppNavigation(
                apiInterface = apiInterface,
                notificationLaunch = notificationLaunch
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationLaunch = readNotificationLaunch(intent)
    }

    private fun readNotificationLaunch(intent: Intent?): NotificationLaunch? {
        if (intent == null || Data(this).accessToken.isNullOrBlank()) return null
        val keys = listOf(
            "notification_key", "google.message_id", "title", "body", "type", "postId", "id",
            "notification_type", "notification_id", "gcm.notification.title", "gcm.notification.body",
            "gcm.notification.type", "gcm.notification.postId", "gcm.n.title", "gcm.n.body"
        )
        val values = keys.mapNotNull { key -> intent.getStringExtra(key)?.let { key to it } }.toMap()
        val incoming = incomingPush(values) ?: return null
        val saved = PushNotificationStore.add(this, incoming.title, incoming.body, incoming.type, incoming.targetId, incoming.key)
        return NotificationLaunch(saved.key, routeForNotification(saved.type, saved.targetId))
    }
}

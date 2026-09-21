package com.example.shinhan_qna_aos

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

data class PushNotification(
    val key: String,
    val title: String,
    val body: String,
    val receivedAt: Long,
    val type: String?,
    val targetId: String?,
    val isRead: Boolean = false
)

object PushNotificationStore {
    private const val PREFS_NAME = "push_notifications"
    private const val KEY_ITEMS = "items"
    private const val MAX_ITEMS = 100
    private val gson = Gson()
    private val listType = object : TypeToken<List<PushNotification>>() {}.type

    fun all(context: Context): List<PushNotification> = runCatching {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ITEMS, null) ?: return emptyList()
        gson.fromJson<List<PushNotification>>(json, listType) ?: emptyList()
    }.getOrDefault(emptyList())

    @Synchronized
    fun add(context: Context, title: String, body: String, type: String?, targetId: String?, key: String? = null): PushNotification {
        val existing = key?.let { candidate -> all(context).firstOrNull { it.key == candidate } }
        if (existing != null) return existing
        val notification = PushNotification(key ?: UUID.randomUUID().toString(), title, body, System.currentTimeMillis(), type, targetId)
        save(context, (listOf(notification) + all(context)).take(MAX_ITEMS))
        return notification
    }

    @Synchronized
    fun markRead(context: Context, key: String) {
        save(context, all(context).map { if (it.key == key) it.copy(isRead = true) else it })
    }

    @Synchronized
    fun remove(context: Context, key: String) {
        save(context, all(context).filterNot { it.key == key })
    }

    @Synchronized
    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        NotificationManagerCompat.from(context).cancelAll()
    }

    private fun save(context: Context, items: List<PushNotification>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_ITEMS, gson.toJson(items)).apply()
    }
}

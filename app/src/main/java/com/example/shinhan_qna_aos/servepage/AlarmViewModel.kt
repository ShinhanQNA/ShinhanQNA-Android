package com.example.shinhan_qna_aos.servepage

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.shinhan_qna_aos.PushNotification
import com.example.shinhan_qna_aos.PushNotificationStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlarmViewModel(context: Context) : ViewModel() {
    private val appContext = context.applicationContext
    private val _notifications = MutableStateFlow(PushNotificationStore.all(appContext))
    val notifications = _notifications.asStateFlow()

    fun refresh() {
        _notifications.value = PushNotificationStore.all(appContext)
    }

    fun markRead(notification: PushNotification) {
        markRead(notification.key)
    }

    fun markRead(key: String) {
        PushNotificationStore.markRead(appContext, key)
        refresh()
    }

    fun delete(notification: PushNotification) {
        PushNotificationStore.remove(appContext, notification.key)
        refresh()
    }
}

package com.example.shinhan_qna_aos

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataInstrumentedTest {
    @Test
    fun storesTokensEncryptedAtRest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val data = Data(context)

        try {
            data.saveTokens("access-token", "refresh-token", 60)

            val storedAccessToken = context
                .getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
                .getString("ACCESS_TOKEN", null)

            assertNotEquals("access-token", storedAccessToken)
            assertEquals("access-token", data.accessToken)
        } finally {
            data.clearAccountData()
        }
    }

    @Test
    fun clearsAccountDataAfterWithdrawal() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val data = Data(context)

        try {
            data.saveTokens("access-token", "refresh-token", 60)
            data.userStatus = "가입 완료"
            data.userName = "테스트 사용자"
            data.userEmail = "test@example.com"
            data.studentCertified = true
            data.isAppealCompleted = true
            data.isReapplying = true
            data.isAdmin = true
            PushNotificationStore.add(context, "알림", "내용", "notice", "1")

            data.clearAccountData()

            assertNull(data.accessToken)
            assertNull(data.refreshToken)
            assertEquals(0L, data.accessTokenExpiresAt)
            assertEquals(0L, data.refreshTokenExpiresAt)
            assertNull(data.userStatus)
            assertNull(data.userName)
            assertNull(data.userEmail)
            assertFalse(data.studentCertified)
            assertFalse(data.isAppealCompleted)
            assertFalse(data.isReapplying)
            assertFalse(data.isAdmin)
            assertTrue(PushNotificationStore.all(context).isEmpty())
        } finally {
            data.clearAccountData()
        }
    }

    @Test
    fun savesReadsAndDeletesNotifications() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        PushNotificationStore.clear(context)

        try {
            val notification = PushNotificationStore.add(context, "공지", "새 공지가 있습니다.", "notice", "1")
            assertFalse(PushNotificationStore.all(context).single().isRead)

            PushNotificationStore.markRead(context, notification.key)
            assertTrue(PushNotificationStore.all(context).single().isRead)

            PushNotificationStore.remove(context, notification.key)
            assertTrue(PushNotificationStore.all(context).isEmpty())

            PushNotificationStore.add(context, "공지", "내용", "notice", "1", "message-1")
            PushNotificationStore.add(context, "공지", "내용", "notice", "1", "message-1")
            assertEquals(1, PushNotificationStore.all(context).size)
        } finally {
            PushNotificationStore.clear(context)
        }
    }
}

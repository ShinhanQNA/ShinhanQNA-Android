package com.example.shinhan_qna_aos

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
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
            data.clearTokens()
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
        } finally {
            data.clearAccountData()
        }
    }

}

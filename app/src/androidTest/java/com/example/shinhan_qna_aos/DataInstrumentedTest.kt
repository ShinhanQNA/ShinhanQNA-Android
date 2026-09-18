package com.example.shinhan_qna_aos

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
}

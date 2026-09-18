package com.example.shinhan_qna_aos.API

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class APIRetrofitTest {
    @Test
    fun requireHttpsBaseUrl_rejectsCleartextUrl() {
        assertEquals("https://api.example.com/", requireHttpsBaseUrl("https://api.example.com/"))
        assertThrows(IllegalArgumentException::class.java) {
            requireHttpsBaseUrl("http://api.example.com/")
        }
    }
}

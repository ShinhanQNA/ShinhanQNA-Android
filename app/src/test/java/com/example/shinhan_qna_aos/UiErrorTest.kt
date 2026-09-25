package com.example.shinhan_qna_aos

import com.example.shinhan_qna_aos.API.ApiFailureException
import com.example.shinhan_qna_aos.API.ApiFailureKind
import org.junit.Assert.assertEquals
import org.junit.Test

class UiErrorTest {
    @Test
    fun mapsApiFailuresToSafeUserMessages() {
        assertEquals(
            "로그인이 필요합니다.",
            ApiFailureException(ApiFailureKind.AUTHENTICATION, "원본").userMessage("실패")
        )
        assertEquals(
            "네트워크 연결을 확인해 주세요.",
            ApiFailureException(ApiFailureKind.NETWORK, "원본").userMessage("실패")
        )
        assertEquals(
            "실패",
            ApiFailureException(ApiFailureKind.HTTP, "원본").userMessage("실패")
        )
        assertEquals(
            "실패",
            ApiFailureException(ApiFailureKind.EMPTY_RESPONSE, "원본").userMessage("실패")
        )
        assertEquals("실패", IllegalStateException("원본").userMessage("실패"))
    }
}

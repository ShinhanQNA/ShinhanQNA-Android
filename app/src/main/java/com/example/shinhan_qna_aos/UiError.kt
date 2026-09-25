package com.example.shinhan_qna_aos

import com.example.shinhan_qna_aos.API.ApiFailureException
import com.example.shinhan_qna_aos.API.ApiFailureKind

internal fun Throwable.userMessage(fallback: String): String = when (this) {
    is ApiFailureException -> when (kind) {
        ApiFailureKind.AUTHENTICATION -> "로그인이 필요합니다."
        ApiFailureKind.NETWORK -> "네트워크 연결을 확인해 주세요."
        ApiFailureKind.HTTP,
        ApiFailureKind.EMPTY_RESPONSE -> fallback
    }
    else -> fallback
}

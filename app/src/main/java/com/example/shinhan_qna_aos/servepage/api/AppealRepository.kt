package com.example.shinhan_qna_aos.servepage.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.Data

class AppealRepository(
    private val apiInterface: APIInterface,
    private val data: Data
) {
    //이의신청
    suspend fun appeal(): Result<AppealData> = apiResult {
        apiInterface.Appeal(bearerHeader(data.accessToken))
            .bodyOrThrow("응답 데이터 없음")
    }

    // 차단 이유 조회
    suspend fun blockReason(email: String): Result<BlockReasonData> = apiResult {
        apiInterface.BlockReason(
            bearerHeader(data.accessToken),
            ReasonRequest(email)
        ).bodyOrThrow("차단 이유 데이터 없음")
    }
}

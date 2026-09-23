package com.example.shinhan_qna_aos.servepage.manager.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.Data

class AccessionRepository(
    private val data: Data,
    private val apiInterface: APIInterface
) {

    // 가입 대기중 리스트
    suspend fun loadAccession(): Result<List<AccessionData>> = apiResult {
        val response = apiInterface.Accession(bearerHeader(data.accessToken, "로그인 정보가 없습니다"))
        response.bodyOrThrow(httpMessage = "에러: ${response.code()}")
    }

    // 가입 대기 중 상새 정보
    suspend fun loadAccessionDetail(email: String): Result<AccessionDetailData> = apiResult {
        val response = apiInterface.AccessionDetail(
            bearerHeader(data.accessToken, "로그인 정보가 없습니다"),
            email
        )
        response.bodyOrThrow(httpMessage = "에러: ${response.code()}")
    }

    // 유저 가입 상태
    suspend fun userStatus(email: String, status: String): Result<Unit> = apiResult {
        val response = apiInterface.AdminUserStatus(
            bearerHeader(data.accessToken, "로그인 정보가 없습니다"),
            UserStatusRequest(email, status)
        )
        response.bodyOrThrow(httpMessage = "에러: ${response.code()}")
        Unit
    }
}

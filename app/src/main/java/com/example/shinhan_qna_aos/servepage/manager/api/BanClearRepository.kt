package com.example.shinhan_qna_aos.servepage.manager.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.Data

class BanClearRepository(
    private val apiService: APIInterface,
    private val data: Data
) {
    // 이의제기 신청자 리스트
    suspend fun loadBanClearList(): Result<List<BanClearData>> = apiResult {
        val response = apiService.BanClear(bearerHeader(data.accessToken, "로그인 정보가 없음"))
        response.bodyOrThrow(httpMessage = "에러: ${response.code()}")
    }

    // 이의 제기 신청자 정보
    suspend fun banClearUser(email: String): Result<BanClearUser> = apiResult {
        val response = apiService.BanClearDetail(
            bearerHeader(data.accessToken, "로그인 정보가 없음"),
            email
        )
        response.bodyOrThrow(httpMessage = "에러: ${response.code()}")
    }

    // 이의 제기 사용자의 개별 게시글
    suspend fun banUserPost(email: String, postId: Int): Result<Board> = apiResult {
        val response = apiService.BanClearDetailBoard(
            bearerHeader(data.accessToken, "로그인 정보가 없음"),
            email,
            postId
        )
        response.bodyOrThrow(httpMessage = "에러: ${response.code()}")
    }

    //이의 제기 상태 변경
    suspend fun banStatus(status: String, appealId: Int): Result<BanClearStatusResponse> = apiResult {
        val response = apiService.BanClearStatus(
            bearerHeader(data.accessToken, "로그인 정보가 없음"),
            appealId,
            BanClearStatus(status)
        )
        response.bodyOrThrow(httpMessage = "에러: ${response.code()}")
    }
}

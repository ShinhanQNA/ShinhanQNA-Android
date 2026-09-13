package com.example.shinhan_qna_aos.servepage.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data

class AppealRepository(
    private val apiInterface: APIInterface,
    private val data: Data
) {
    //이의신청
    suspend fun appeal(): Result<AppealData> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        return try {
            val response = apiInterface.Appeal("Bearer $accessToken")
            if (response.isSuccessful) {
                val body = response.body()
                if(body != null) {
                    Result.success(body)
                }else{
                    Result.failure(Exception("응답 데이터 없음"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 차단 이유 조회
    suspend fun blockReason(email: String): Result<BlockReasonData> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        val request = ReasonRequest(email)
        return try {
            val response = apiInterface.BlockReason("Bearer $accessToken", request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("차단 이유 데이터 없음"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
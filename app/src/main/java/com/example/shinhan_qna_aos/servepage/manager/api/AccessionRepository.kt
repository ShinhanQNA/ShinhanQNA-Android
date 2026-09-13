package com.example.shinhan_qna_aos.servepage.manager.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data

class AccessionRepository(
    private val data: Data,
    private val apiInterface: APIInterface
) {

    // 가입 대기중 리스트
    suspend fun loadAccession(): Result<List<AccessionData>> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 정보가 없습니다"))
        try {
            val response = apiInterface.Accession("Bearer $accessToken")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.success(body)
                } else {
                    return Result.failure(Exception("Response body is null"))
                }
            } else {
                return Result.failure(Exception("에러: ${response.code()}"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // 가입 대기 중 상새 정보
    suspend fun loadAccessionDetail(email: String): Result<AccessionDetailData> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 정보가 없습니다"))
        try {
            val response = apiInterface.AccessionDetail("Bearer $accessToken", email)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.success(body)
                } else {
                    return Result.failure(Exception("Response body is null"))
                }
            } else {
                return Result.failure(Exception("에러: ${response.code()}"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // 유저 가입 상태
    suspend fun userStatus(email: String, status: String): Result<Unit> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 정보가 없습니다"))
        try {
            val response = apiInterface.AdminUserStatus(
                "Bearer $accessToken",
                UserStatusRequest(email, status)
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Result.success(Unit)
                } else {
                    return Result.failure(Exception("Response body is null"))
                }
            } else {
                return Result.failure(Exception("에러: ${response.code()}"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
package com.example.shinhan_qna_aos.servepage.manager.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data

class DeclarationRepository (
    private val data:Data,
    private val apiInterface: APIInterface
){

    // 신고 당한 게시물 불러오기
    suspend fun loadDeclaration():Result<List<DeclarationData>> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다"))
        return try {
            val response = apiInterface.Declaration("Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터가 없습니다."))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
            }
        }catch ( e:Exception){
            Result.failure(e)
        }
    }

    // 신고 게시글 반려
    suspend fun declarationReject(reportId: Int): Result<DeclarationResponse> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다"))
        return try {
            val response = apiInterface.DeclarationReject("Bearer $accessToken", DeclarationRequest(reportId))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터가 없습니다."))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "알 수 없는 오류"
                Result.failure(Exception("Error ${response.code()}: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
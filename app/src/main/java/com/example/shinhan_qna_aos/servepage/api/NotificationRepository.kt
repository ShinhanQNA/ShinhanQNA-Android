package com.example.shinhan_qna_aos.servepage.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.main.api.Answer
import com.example.shinhan_qna_aos.main.api.AnswerRequest

class NotificationRepository(
    private val apiInterface: APIInterface,
    private val data: Data
) {
    // 공지 리스트 받아오기 API 호출
    suspend fun getNotification(): Result<List<Notices>> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        return try {
            val response = apiInterface.Notification("Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터가 없습니다."))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 답변 작성하기
    suspend fun NoticesWrite(title: String, content: String): Result<Notices> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        val request = NoticesRequest(title, content)
        return try {
            val response = apiInterface.NoticesWritePost("Bearer $accessToken",request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터가 없습니다."))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 공지 수정하기
    suspend fun updateNoticesPost(
        id: String,
        title: String,
        content: String,
    ): Result<Notices> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        val request = NoticesRequest(title = title, content = content)
        return try {

            val response = apiInterface.UpdateNoticesPost(
                accessToken = "Bearer $accessToken",
                id = id.toInt(),
                noticesRequest = request
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 공지 삭제
    suspend fun NoticesDelete(id: Int): Result<Unit> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다"))

        return try {
            val response = apiInterface.DeleteNoticesPost("Bearer $accessToken", id)
            if (response.isSuccessful) {
                Result.success(Unit) // response.body() 체크 없이 성공 처리
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

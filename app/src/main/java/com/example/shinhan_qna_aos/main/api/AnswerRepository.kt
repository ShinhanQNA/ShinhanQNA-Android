package com.example.shinhan_qna_aos.main.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AnswerRepository(
    private val apiInterface: APIInterface,
    private val data: Data
) {
    // 답변 리스트 받아오기 API 호출
    suspend fun getAnswers(): Result<List<Answer>> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        return try {
            val response = apiInterface.AnswerPost("Bearer $accessToken")
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
    // 답변 작성하기 api
    suspend fun AnswerWrite(title: String, content: String): Result<Answer> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        val request = AnswerRequest(title, content)
        return try {
            val response = apiInterface.AnswerWritePost("Bearer $accessToken",request)
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

    // 답변 수정하기
    suspend fun updateAnswerPost(
        id: String,
        title: String,
        content: String,
    ): Result<Answer> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        val request = AnswerRequest(title = title, content = content)
        return try {

            val response = apiInterface.UpdateAnswerPost(
                accessToken = "Bearer $accessToken",
                id = id.toInt(),
                answerRequest = request
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

    // 답변 삭제
    suspend fun AnswerDelete(id: Int): Result<Unit> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다"))

        return try {
            val response = apiInterface.DeleteAnswerPost("Bearer $accessToken", id)
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

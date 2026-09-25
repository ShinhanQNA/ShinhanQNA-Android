package com.example.shinhan_qna_aos.main.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.API.successOrThrow
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
    suspend fun getAnswers(): Result<List<Answer>> = apiResult {
        apiInterface.AnswerPost(bearerHeader(data.accessToken)).bodyOrThrow()
    }
    // 답변 작성하기 api
    suspend fun AnswerWrite(title: String, content: String): Result<Answer> = apiResult {
        apiInterface.AnswerWritePost(
            bearerHeader(data.accessToken),
            AnswerRequest(title, content)
        ).bodyOrThrow()
    }

    // 답변 수정하기
    suspend fun updateAnswerPost(
        id: String,
        title: String,
        content: String,
    ): Result<Answer> = apiResult {
        apiInterface.UpdateAnswerPost(
            accessToken = bearerHeader(data.accessToken),
            id = id.toInt(),
            answerRequest = AnswerRequest(title = title, content = content)
        ).bodyOrThrow("서버 오류가 발생했습니다.")
    }

    // 답변 삭제
    suspend fun AnswerDelete(id: Int): Result<Unit> = apiResult {
        apiInterface.DeleteAnswerPost(bearerHeader(data.accessToken), id).successOrThrow()
    }
}

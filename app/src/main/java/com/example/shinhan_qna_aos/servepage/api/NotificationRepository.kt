package com.example.shinhan_qna_aos.servepage.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.API.successOrThrow
import com.example.shinhan_qna_aos.Data

class NotificationRepository(
    private val apiInterface: APIInterface,
    private val data: Data
) {
    // 공지 리스트 받아오기 API 호출
    suspend fun getNotification(): Result<List<Notices>> = apiResult {
        apiInterface.Notification(bearerHeader(data.accessToken)).bodyOrThrow()
    }

    // 답변 작성하기
    suspend fun NoticesWrite(title: String, content: String): Result<Notices> = apiResult {
        apiInterface.NoticesWritePost(
            bearerHeader(data.accessToken),
            NoticesRequest(title, content)
        ).bodyOrThrow()
    }

    // 공지 수정하기
    suspend fun updateNoticesPost(
        id: String,
        title: String,
        content: String,
    ): Result<Notices> = apiResult {
        apiInterface.UpdateNoticesPost(
            accessToken = bearerHeader(data.accessToken),
            id = id.toInt(),
            noticesRequest = NoticesRequest(title = title, content = content)
        ).bodyOrThrow("서버 오류가 발생했습니다.")
    }

    // 공지 삭제
    suspend fun NoticesDelete(id: Int): Result<Unit> = apiResult {
        apiInterface.DeleteNoticesPost(bearerHeader(data.accessToken), id).successOrThrow()
    }
}

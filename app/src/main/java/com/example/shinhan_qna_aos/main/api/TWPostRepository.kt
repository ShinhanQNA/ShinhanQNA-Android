package com.example.shinhan_qna_aos.main.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.debugLog
import java.time.LocalDate

class TWPostRepository (
    private val apiInterface: APIInterface,
    private val data: Data
) {
    // 년도를 넘겨 3주 의견 데이터 호출 suspend 함수
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchThreeWeekOpinions(): Result<List<GroupID>> = apiResult {
        apiInterface.ThreeWeekPost(bearerHeader(data.accessToken), LocalDate.now().year)
            .bodyOrThrow()
            .also { debugLog("TWPostRepository", "API 호출에 성공했습니다: 개수=${it.size}") }
    }

    suspend fun fetchGroupDetail(groupId: Int, sort: String = "date"): Result<TWPostData> = apiResult {
        apiInterface.ThreeWeekPostDetail(bearerHeader(data.accessToken), groupId, sort)
            .bodyOrThrow("응답 데이터 없음")
    }

    suspend fun putStatus(groupId: Int, status: String): Result<GroupStatus> = apiResult {
        apiInterface.ThreeWeekStatus(
            bearerHeader(data.accessToken),
            groupId,
            GroupStatusRequest(status)
        ).bodyOrThrow("응답 데이터 없음")
    }
}

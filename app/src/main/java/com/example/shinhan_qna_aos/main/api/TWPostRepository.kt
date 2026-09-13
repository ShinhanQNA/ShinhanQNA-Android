package com.example.shinhan_qna_aos.main.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data
import retrofit2.Response
import java.time.LocalDate

class TWPostRepository (
    private val apiInterface: APIInterface,
    private val data: Data
) {
    // 년도를 넘겨 3주 의견 데이터 호출 suspend 함수
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchThreeWeekOpinions(): Result<List<GroupID>> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        val year = LocalDate.now().year
        return try {
            val response = apiInterface.ThreeWeekPost("Bearer $accessToken", year)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("TWPostRepository", "API 호출 성공 - 데이터 수: ${it.size}")
                    Result.success(it)
                } ?: run {
                    Log.e("TWPostRepository", "API 호출 성공했으나 body가 null")
                    Result.failure(Exception("응답 데이터가 없습니다."))
                }
            } else {
                Log.e(
                    "TWPostRepository",
                    "API 호출 실패 - 코드: ${response.code()}, 메시지: ${response.message()}"
                )
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("TWPostRepository", "API 호출 중 예외 발생", e)
            Result.failure(e)
        }
    }

    suspend fun fetchGroupDetail(groupId: Int, sort: String = "date"): Result<TWPostData> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))
        return try {
            val response = apiInterface.ThreeWeekPostDetail("Bearer $accessToken", groupId, sort)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("TWPostRepository", "그룹 상세 API 호출 중 예외", e)
            Result.failure(e)
        }
    }

    suspend fun putStatus(groupId: Int, status: String): Result<GroupStatus> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다"))
        val groupStatusRequest = GroupStatusRequest(status)
        return try {
            val response = apiInterface.ThreeWeekStatus("Bearer $accessToken", groupId, groupStatusRequest)
            if (response.isSuccessful)
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            else
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
        } catch (e: Exception) {
            Log.e("TWPostRepository", "그룹 상태 변경 API 호출 중 예외", e)
            Result.failure(e)
        }
    }
}
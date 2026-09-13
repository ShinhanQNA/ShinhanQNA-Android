package com.example.shinhan_qna_aos.servepage.manager.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data

class BanClearRepository(
    private val apiService: APIInterface,
    private val data: Data
) {
    // 이의제기 신청자 리스트
    suspend fun loadBanClearList(): Result<List<BanClearData>> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 정보가 없음"))
        try {
            val response = apiService.BanClear("Bearer $accessToken")
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

    // 이의 제기 신청자 정보
    suspend fun banClearUser(email: String): Result<BanClearUser> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 정보가 없음"))
        try {
            val response = apiService.BanClearDetail("Bearer $accessToken", email)
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

    // 이의 제기 사용자의 개별 게시글
    suspend fun banUserPost(email: String, postId: Int): Result<Board> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 정보가 없음"))
        try {
            val respose = apiService.BanClearDetailBoard("Bearer $accessToken", email, postId)
            if (respose.isSuccessful) {
                val body = respose.body()
                if (body != null) {
                    return Result.success(body)
                } else {
                    return Result.failure(Exception("Response body is null"))
                }
            } else {
                return Result.failure(Exception("에러: ${respose.code()}"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    //이의 제기 상태 변경
    suspend fun banStatus(status:String, appealId : Int):Result<BanClearStatusResponse>{
        val accesstoken = data.accessToken ?: return Result.failure(Exception("로그인 정보가 없음"))
        try {
            val response = apiService.BanClearStatus("Bearer $accesstoken",appealId, BanClearStatus(status) )
            if (response.isSuccessful) {
                val body = response.body()
                if (body!=null){
                    return Result.success(body)
                }else{
                    return Result.failure(Exception("Response body is null"))
                }
            }else{
                return Result.failure(Exception("에러: ${response.code()}"))
                }
            }catch (e:Exception){
                return Result.failure(e)
        }
    }
}
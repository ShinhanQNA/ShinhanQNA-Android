package com.example.shinhan_qna_aos.login.api

import com.google.gson.annotations.SerializedName


//  요청/응답 DTO
// 관리자 API 전달
data class AdminRequest(
    val id : String,
    val password : String
)
// API 리프래시 토큰 전달
data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)
// API 응답 받는 데이터
data class LoginTokensResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int
)

// -----------------------------
// 내부 상태
// -----------------------------
data class ManagerLoginData(
    val managerId: String = "",
    val managerPassword: String = ""
)

// 로그인 결과 표현용
sealed class LoginResult {
    data object Idle : LoginResult()
    data class Success(
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Int
    ) : LoginResult()

    data class Failure(
        val status: Int,
        val message: String
    ) : LoginResult()
}

data class LogoutData(
    val message: String
)

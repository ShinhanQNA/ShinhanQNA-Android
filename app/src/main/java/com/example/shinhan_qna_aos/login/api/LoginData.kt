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

data class AuthState(
    val session: AuthSession = AuthSession.Checking,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isAuthenticated: Boolean
        get() = session !is AuthSession.Checking && session !is AuthSession.SignedOut

    val isAdmin: Boolean
        get() = session is AuthSession.Admin

    val canOpenNotifications: Boolean
        get() = session is AuthSession.Active ||
            session is AuthSession.Warned ||
            session is AuthSession.Admin
}

sealed interface AuthSession {
    data object Checking : AuthSession
    data object SignedOut : AuthSession
    data object NeedsStudentInfo : AuthSession
    data object Pending : AuthSession
    data object Rejected : AuthSession
    data object Reapplying : AuthSession
    data object Active : AuthSession
    data object Warned : AuthSession
    data class Blocked(val appealSubmitted: Boolean) : AuthSession
    data object Admin : AuthSession
}

internal fun authSessionForUser(
    status: String,
    studentCertified: Boolean?,
    appealCompleted: Boolean,
    reapplying: Boolean = false
): AuthSession = when {
    reapplying && status in setOf("가입 대기 중", "가입 거절", "거절") -> AuthSession.Reapplying
    status == "차단" -> AuthSession.Blocked(appealCompleted)
    status == "경고" -> AuthSession.Warned
    status == "거절" || status == "가입 거절" -> AuthSession.Rejected
    status == "가입 완료" -> AuthSession.Active
    status == "가입 대기 중" -> AuthSession.Pending
    studentCertified == false -> AuthSession.NeedsStudentInfo
    else -> AuthSession.NeedsStudentInfo
}

data class LogoutData(
    val message: String
)

package com.example.shinhan_qna_aos.login.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data

/**
 * AuthRepository
 * - 서버 통신(APIInterface) + 로컬 토큰 저장(LoginManager) 책임
 * - ViewModel은 Repository의 함수를 호출만 함
 */
class AuthRepository(
    private val apiInterface: APIInterface,
    private val data: Data // 토큰, 관리자 여부 저장 객체
) {
    @Volatile
    private var isRefreshing = false // 재발급 중복 방지용 플래그

    //  관리자 로그인
    suspend fun loginAdmin(id: String, password: String): Result<LoginTokensResponse> {
        return runCatching {
            val response = apiInterface.AdminLoginData(AdminRequest(id, password))
            if (response.isSuccessful) {
                response.body()?.also {
                    saveTokens(it, isAdmin = true) // 관리자 여부 저장
                } ?: throw Exception("로그인 응답이 비어있습니다.")
            } else {
                throw Exception("로그인 실패: ${response.code()} ${response.message()}")
            }
        }
    }

    //  카카오 로그인
    suspend fun loginWithKakao(accessToken: String): Result<LoginTokensResponse> {
        return runCatching {
            val response = apiInterface.KakaoAuthCode(accessToken)
            if (response.isSuccessful) {
                response.body()?.also {
                    saveTokens(it, isAdmin = false) // 기본적으로 일반 사용자
                } ?: throw Exception("응답 데이터 없음")
            } else {
                throw Exception("로그인 실패: ${response.code()} ${response.message()}")
            }
        }
    }

    //  구글 로그인
    suspend fun loginWithGoogle(authCode: String): Result<LoginTokensResponse> {
        return runCatching {
            val response = apiInterface.GoogleAuthCode(authCode)
            if (response.isSuccessful) {
                response.body()?.also {
                    saveTokens(it, isAdmin = false)
                } ?: throw Exception("응답 데이터 없음")
            } else {
                throw Exception("로그인 실패: ${response.code()} ${response.message()}")
            }
        }
    }

    /**
     * 토큰 재발급 처리 (중복 호출 방지 락 적용)
     * @return true: 재발급 성공 혹은 Access Token 유효 / false: 재발급 실패 (재로그인 필요)
     */
    suspend fun refreshTokenIfNeeded(): Boolean {
        // 이미 재발급 중이면 중복 실행 방지
        if (isRefreshing) {
            return false
        }

        // Access Token이 유효하면 true 반환, 재발급 불필요함
        if (!data.isAccessTokenExpired()) {
            return true
        }

        val refreshToken = data.refreshToken ?: return false
        if (data.isRefreshTokenExpired()) return false

        isRefreshing = true // 재발급 시작 플래그 설정
        try {
            val response = apiInterface.ReToken(RefreshTokenRequest(refreshToken))
            return if (response.isSuccessful) {
                response.body()?.let {
                    saveTokens(it, isAdmin = data.isAdmin) // 기존 관리자 여부 유지하며 토큰 저장
                }
                true
            } else {
                false
            }
        } finally {
            isRefreshing = false // 재발급 완료 후 플래그 해제
        }
    }

    //  토큰 및 관리자 여부 저장
    private fun saveTokens(tokens: LoginTokensResponse, isAdmin: Boolean = false) {
        data.saveTokens(tokens.accessToken, tokens.refreshToken, tokens.expiresIn)
        data.isAdmin = isAdmin
    }

    /**
     * 로그아웃
     */
    suspend fun logout(): Result<LogoutData> {
        val refreshToken = data.refreshToken
        return try {
            val response = apiInterface.LogOut("Bearer $refreshToken")
            if (response.isSuccessful) {
                response.body()?.let {
                    data.clearTokens()
                    Result.success(it)
                } ?: Result.failure(Exception("서버 응답이 비어있습니다."))
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 회원 탈퇴
     */
    suspend fun cancleMember(): Result<LogoutData> {
        val accessToken = data.accessToken
        return try {
            val response = apiInterface.CancelMember("Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()?.let {
                    data.clearTokens()
                    Result.success(it)
                } ?: Result.failure(Exception("서버 응답이 비어있습니다."))
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()} $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

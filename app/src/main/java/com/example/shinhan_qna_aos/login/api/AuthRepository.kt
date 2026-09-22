package com.example.shinhan_qna_aos.login.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * AuthRepository
 * - 서버 통신(APIInterface) + 로컬 토큰 저장(LoginManager) 책임
 * - ViewModel은 Repository의 함수를 호출만 함
 */
class AuthRepository(
    private val apiInterface: APIInterface,
    private val data: Data // 토큰, 관리자 여부 저장 객체
) {
    private val refreshMutex = Mutex()

    //  관리자 로그인
    suspend fun loginAdmin(id: String, password: String): Result<LoginTokensResponse> {
        return runCatching {
            val response = apiInterface.AdminLoginData(AdminRequest(id, password))
            if (response.isSuccessful) {
                response.body()?.also {
                    saveTokens(it, isAdmin = true) // 관리자 여부 저장
                } ?: throw Exception("로그인 응답이 비어있습니다.")
            } else {
                throw Exception("로그인에 실패했습니다.")
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
                throw Exception("로그인에 실패했습니다.")
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
                throw Exception("로그인에 실패했습니다.")
            }
        }
    }

    /**
     * 토큰 재발급 처리 (중복 호출 방지 락 적용)
     * @return true: 재발급 성공 혹은 Access Token 유효 / false: 재발급 실패 (재로그인 필요)
     */
    suspend fun refreshTokenIfNeeded(): Result<Boolean> = refreshMutex.withLock {
        if (!data.isAccessTokenExpired()) return@withLock Result.success(true)

        val refreshToken = data.refreshToken ?: return@withLock Result.success(false)
        if (data.isRefreshTokenExpired()) return@withLock Result.success(false)

        runCatching {
            val response = apiInterface.ReToken(RefreshTokenRequest(refreshToken))
            when {
                response.isSuccessful -> {
                    val tokens = response.body() ?: throw Exception("토큰 재발급 응답이 비어있습니다.")
                    saveTokens(tokens, isAdmin = data.isAdmin)
                    true
                }
                response.code() in setOf(400, 401, 403) -> false
                else -> throw Exception("토큰 재발급에 실패했습니다.")
            }
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
                data.clearAccountData()
                Result.success(response.body() ?: LogoutData("로그아웃되었습니다."))
            } else {
                Result.failure(Exception("서버 오류가 발생했습니다."))
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
                data.clearAccountData()
                Result.success(response.body() ?: LogoutData("회원 탈퇴가 완료되었습니다."))
            } else {
                Result.failure(Exception("서버 오류가 발생했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

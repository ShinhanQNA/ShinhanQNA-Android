package com.example.shinhan_qna_aos.login.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.API.successOrThrow
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
    suspend fun loginAdmin(id: String, password: String): Result<LoginTokensResponse> = apiResult {
        apiInterface.AdminLoginData(AdminRequest(id, password))
            .bodyOrThrow("로그인 응답이 비어있습니다.", "로그인에 실패했습니다.")
            .also { saveTokens(it, isAdmin = true) }
    }

    //  카카오 로그인
    suspend fun loginWithKakao(accessToken: String): Result<LoginTokensResponse> = apiResult {
        apiInterface.KakaoAuthCode(accessToken)
            .bodyOrThrow("응답 데이터 없음", "로그인에 실패했습니다.")
            .also { saveTokens(it, isAdmin = false) }
    }

    //  구글 로그인
    suspend fun loginWithGoogle(authCode: String): Result<LoginTokensResponse> = apiResult {
        apiInterface.GoogleAuthCode(authCode)
            .bodyOrThrow("응답 데이터 없음", "로그인에 실패했습니다.")
            .also { saveTokens(it, isAdmin = false) }
    }

    /**
     * 토큰 재발급 처리 (중복 호출 방지 락 적용)
     * @return true: 재발급 성공 혹은 Access Token 유효 / false: 재발급 실패 (재로그인 필요)
     */
    suspend fun refreshTokenIfNeeded(): Result<Boolean> = refreshMutex.withLock {
        if (!data.isAccessTokenExpired()) return@withLock Result.success(true)

        val refreshToken = data.refreshToken ?: return@withLock Result.success(false)
        if (data.isRefreshTokenExpired()) return@withLock Result.success(false)

        apiResult {
            val response = apiInterface.ReToken(RefreshTokenRequest(refreshToken))
            when {
                response.code() in setOf(400, 401, 403) -> false
                else -> response.bodyOrThrow(
                    emptyMessage = "토큰 재발급 응답이 비어있습니다.",
                    httpMessage = "토큰 재발급에 실패했습니다."
                ).let {
                    saveTokens(it, isAdmin = data.isAdmin)
                    true
                }
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
    suspend fun logout(): Result<LogoutData> = apiResult {
        val response = apiInterface.LogOut(
            bearerHeader(data.refreshToken, "로그인 결과가 없습니다.")
        )
        response.successOrThrow()
        data.clearAccountData()
        response.body() ?: LogoutData("로그아웃되었습니다.")
    }

    /**
     * 회원 탈퇴
     */
    suspend fun cancleMember(): Result<LogoutData> = apiResult {
        val response = apiInterface.CancelMember(bearerHeader(data.accessToken))
        response.successOrThrow()
        data.clearAccountData()
        response.body() ?: LogoutData("회원 탈퇴가 완료되었습니다.")
    }
}

package com.example.shinhan_qna_aos.login.api

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.Data
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val data: Data // 토큰 저장소 (예: SharedPreferences or DB)
) : ViewModel() {

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> get() = _loginResult

    init {
        // 필요 시 리프래시 토큰으로 갱신
        tryRefreshTokenIfNeeded()
    }

    //  카카오 로그인
    fun loginWithKakao(context: Context) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                _loginResult.value =
                    LoginResult.Failure(-1, error.localizedMessage ?: "카카오 로그인 실패")
            } else if (token != null) {
                viewModelScope.launch {
                    repository.loginWithKakao(token.accessToken.trim())
                        .onSuccess {
                            _loginResult.value =
                                LoginResult.Success(it.accessToken, it.refreshToken, it.expiresIn)
                        }
                        .onFailure { e ->
                            _loginResult.value =
                                LoginResult.Failure(-1, e.localizedMessage ?: "서버 통신 실패")
                        }
                }
            }
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    //  구글 로그인 (AuthCode 전달)
    fun sendGoogleAuthCodeToServer(authCode: String) {
        viewModelScope.launch {
            repository.loginWithGoogle(authCode)
                .onSuccess {
                    _loginResult.value =
                        LoginResult.Success(it.accessToken, it.refreshToken, it.expiresIn)
                }
                .onFailure { e ->
                    _loginResult.value =
                        LoginResult.Failure(-1, e.localizedMessage ?: "구글 로그인 실패")
                }
        }
    }

    /**
     * 로그아웃 시도
     */
    fun logout() {
        viewModelScope.launch {
            repository.logout()
                .onSuccess {
                    _loginResult.value = LoginResult.Idle
                }
                .onFailure { e ->
                    // 로그아웃 실패처리 예: 로그 출력 또는 UI 알림
                    Log.e("Logout", "로그아웃 실패: ${e.localizedMessage}")
                }
        }
    }

    /**
     * 회원 탈퇴 시도
     */
    fun CancleMemeber() {
        viewModelScope.launch {
            repository.cancleMember()
                .onSuccess {
                    _loginResult.value = LoginResult.Idle
                }
                .onFailure { e ->
                    // 로그아웃 실패처리 예: 로그 출력 또는 UI 알림
                    Log.e("cancle", "회원탈퇴 실패: ${e.localizedMessage}")
                }
        }
    }

    /**
     * 토큰 재발급 시도: Access Token 유효/재발급 성공 시 로그인 성공 상태로 업데이트,
     * 실패 시 로그인 실패 상태로 변경 (재로그인 필요)
     */
    fun tryRefreshTokenIfNeeded() {
        viewModelScope.launch {
            val access = data.accessToken
            if (!access.isNullOrBlank() && !data.isAccessTokenExpired()) {
                // Access Token 유효 → 즉시 로그인 성공 상태 변경
                _loginResult.value = LoginResult.Success(
                    accessToken = access,
                    refreshToken = data.refreshToken.orEmpty(),
                    expiresIn = ((data.accessTokenExpiresAt - System.currentTimeMillis()) / 1000).toInt()
                )
            } else {
                // 토큰 만료 또는 없으면 재발급 시도
                if (repository.refreshTokenIfNeeded()) {
                    val newAccess = data.accessToken ?: ""
                    _loginResult.value = LoginResult.Success(
                        accessToken = newAccess,
                        refreshToken = data.refreshToken.orEmpty(),
                        expiresIn = ((data.accessTokenExpiresAt - System.currentTimeMillis()) / 1000).toInt()
                    )
                } else {
                    _loginResult.value = LoginResult.Failure(-1, "재로그인 필요")
                }
            }
        }
    }
}

package com.example.shinhan_qna_aos.login.api

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.info.api.InfoRepository
import com.example.shinhan_qna_aos.info.api.UserCheckResponse
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val infoRepository: InfoRepository,
    private val data: Data
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    var managerLoginData by mutableStateOf(ManagerLoginData())
        private set

    private var authJob: Job? = null
    private var requestVersion = 0L
    private var awaitingUserAfterLogin = false

    init {
        refreshSession()
        viewModelScope.launch {
            while (isActive) {
                delay(60_000)
                refreshSession()
            }
        }
    }

    fun loginWithKakao(context: Context) {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            when {
                error != null -> showLoginError(error.localizedMessage ?: "카카오 로그인 실패")
                token != null -> loginUser { authRepository.loginWithKakao(token.accessToken.trim()) }
                else -> showLoginError("카카오 로그인 실패")
            }
        }
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    fun loginWithGoogle(authCode: String) {
        loginUser { authRepository.loginWithGoogle(authCode) }
    }

    fun onAdminIdChange(id: String) {
        managerLoginData = managerLoginData.copy(managerId = id)
    }

    fun onAdminPasswordChange(password: String) {
        managerLoginData = managerLoginData.copy(managerPassword = password)
    }

    fun loginAdmin() = launchAuthRequest(force = true) { version ->
        setLoading()
        authRepository.loginAdmin(managerLoginData.managerId, managerLoginData.managerPassword)
            .onSuccess {
                awaitingUserAfterLogin = false
                data.clearCachedUserState()
                managerLoginData = ManagerLoginData()
                applyState(version, AuthSession.Admin)
            }
            .onFailure { applyLoginError(version, it.localizedMessage ?: "관리자 로그인 실패") }
    }

    fun refreshSession() = launchAuthRequest(force = false) { version ->
        if (data.accessToken.isNullOrBlank() && data.refreshToken.isNullOrBlank()) {
            applyState(version, AuthSession.SignedOut)
            return@launchAuthRequest
        }

        setLoading()
        authRepository.refreshTokenIfNeeded()
            .onSuccess { authenticated ->
                when {
                    !authenticated -> {
                        data.clearAccountData()
                        applyState(version, AuthSession.SignedOut)
                    }
                    data.isAdmin -> applyState(version, AuthSession.Admin)
                    else -> loadUser(version)
                }
            }
            .onFailure { applyRefreshError(version, it.localizedMessage ?: "세션 확인에 실패했습니다.") }
    }

    fun onAppResumed() {
        refreshSession()
    }

    fun beginReapplication() {
        invalidateAuthRequest()
        data.isReapplying = true
        _state.value = AuthState(session = AuthSession.Reapplying)
    }

    fun onStudentInfoSubmitted(reapplying: Boolean) {
        if (reapplying) {
            invalidateAuthRequest()
            data.isReapplying = false
            _state.value = AuthState(session = AuthSession.Pending)
        }
        refreshUserStatus()
    }

    fun onAppealSubmitted() {
        invalidateAuthRequest()
        data.isAppealCompleted = true
        if (_state.value.session is AuthSession.Blocked) {
            _state.value = AuthState(session = AuthSession.Blocked(appealSubmitted = true))
        }
    }

    fun logout() = launchAuthRequest(force = true) { version ->
        awaitingUserAfterLogin = false
        setLoading()
        authRepository.logout()
            .onSuccess { applyState(version, AuthSession.SignedOut) }
            .onFailure { applyRefreshError(version, it.localizedMessage ?: "로그아웃에 실패했습니다.") }
    }

    fun cancelMember() = launchAuthRequest(force = true) { version ->
        awaitingUserAfterLogin = false
        setLoading()
        authRepository.cancleMember()
            .onSuccess { applyState(version, AuthSession.SignedOut) }
            .onFailure { applyRefreshError(version, it.localizedMessage ?: "회원 탈퇴에 실패했습니다.") }
    }

    private fun loginUser(request: suspend () -> Result<LoginTokensResponse>) =
        launchAuthRequest(force = true) { version ->
            setLoading()
            request()
                .onSuccess {
                    awaitingUserAfterLogin = true
                    loadUser(version)
                }
                .onFailure { applyLoginError(version, it.localizedMessage ?: "로그인에 실패했습니다.") }
        }

    private fun refreshUserStatus() = launchAuthRequest(force = true) { version ->
        setLoading()
        loadUser(version)
    }

    private suspend fun loadUser(version: Long) {
        infoRepository.checkUserStatus()
            .onSuccess { updateUser(version, it.user) }
            .onFailure { applyRefreshError(version, it.localizedMessage ?: "사용자 상태 조회에 실패했습니다.") }
    }

    private fun updateUser(version: Long, user: UserCheckResponse) {
        if (version != requestVersion) return
        val previousEmail = data.userEmail
        if (!previousEmail.isNullOrBlank() && !user.email.isNullOrBlank() && previousEmail != user.email) {
            data.clearCachedUserState()
        }

        data.userStatus = user.status
        data.userName = user.name
        data.userEmail = user.email
        data.studentCertified = user.studentCertified == true
        if (user.status !in setOf("가입 대기 중", "가입 거절", "거절")) data.isReapplying = false
        if (user.status == "가입 완료") data.clearAppealCompleted()
        awaitingUserAfterLogin = false

        applyState(
            version,
            authSessionForUser(
                status = user.status,
                studentCertified = user.studentCertified,
                appealCompleted = data.isAppealCompleted,
                reapplying = data.isReapplying
            )
        )
    }

    private fun launchAuthRequest(force: Boolean, block: suspend (Long) -> Unit) {
        if (!force && authJob?.isActive == true) return
        authJob?.cancel()
        val version = ++requestVersion
        authJob = viewModelScope.launch { block(version) }
    }

    private fun setLoading() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
    }

    private fun applyState(version: Long, session: AuthSession) {
        if (version == requestVersion) _state.value = AuthState(session = session)
    }

    private fun applyLoginError(version: Long, message: String) {
        if (version == requestVersion) {
            awaitingUserAfterLogin = false
            _state.value = AuthState(session = AuthSession.SignedOut, errorMessage = message)
        }
    }

    private fun applyRefreshError(version: Long, message: String) {
        if (version != requestVersion) return
        val current = _state.value.session
        val fallback = when {
            awaitingUserAfterLogin -> AuthSession.NeedsStudentInfo
            current is AuthSession.Checking -> cachedSession()
            else -> current
        }
        _state.value = AuthState(session = fallback, errorMessage = message)
    }

    private fun showLoginError(message: String) {
        invalidateAuthRequest()
        awaitingUserAfterLogin = false
        _state.value = AuthState(session = AuthSession.SignedOut, errorMessage = message)
    }

    private fun invalidateAuthRequest() {
        authJob?.cancel()
        requestVersion++
    }

    private fun cachedSession(): AuthSession = when {
        data.isAdmin -> AuthSession.Admin
        !data.userStatus.isNullOrBlank() -> authSessionForUser(
            status = data.userStatus.orEmpty(),
            studentCertified = data.studentCertified,
            appealCompleted = data.isAppealCompleted,
            reapplying = data.isReapplying
        )
        else -> AuthSession.NeedsStudentInfo
    }
}

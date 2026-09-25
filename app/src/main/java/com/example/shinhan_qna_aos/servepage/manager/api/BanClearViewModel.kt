package com.example.shinhan_qna_aos.servepage.manager.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BanClearUiState(
    val banClearList: List<BanClearData> = emptyList(),
    val banClearDetail: BanClearUser? = null,
    val banClearPost: Board? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BanClearViewModel(private val banClearRepository: BanClearRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(BanClearUiState())
    val uiState = _uiState.asStateFlow()

    fun LoadBanClearList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            banClearRepository.loadBanClearList()
                .onSuccess { users ->
                    _uiState.update { it.copy(banClearList = users, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("이의신청 목록을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun LoadBanClearDetail(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            banClearRepository.banClearUser(email)
                .onSuccess { detail ->
                    _uiState.update { it.copy(banClearDetail = detail, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("이의신청 상세 정보를 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun LoadBanClearPost(email: String, postId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            banClearRepository.banUserPost(email, postId)
                .onSuccess { post ->
                    _uiState.update { it.copy(banClearPost = post, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("이의신청 게시글을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun banStatus(status: String, appealId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            banClearRepository.banStatus(status, appealId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("이의신청 상태를 변경하지 못했습니다.")
                        )
                    }
                }
        }
    }
}

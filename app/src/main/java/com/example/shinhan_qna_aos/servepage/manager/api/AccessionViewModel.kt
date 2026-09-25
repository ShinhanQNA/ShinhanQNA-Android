package com.example.shinhan_qna_aos.servepage.manager.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccessionUiState(
    val accessionList: List<AccessionData> = emptyList(),
    val accessionDetail: AccessionDetailData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AccessionViewModel(private val accessionRepository: AccessionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AccessionUiState())
    val uiState = _uiState.asStateFlow()

    fun LoadAccession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            accessionRepository.loadAccession()
                .onSuccess { users ->
                    _uiState.update { it.copy(accessionList = users, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("가입 대기 목록을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun LoadAccessionDetail(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            accessionRepository.loadAccessionDetail(email)
                .onSuccess { detail ->
                    _uiState.update { it.copy(accessionDetail = detail, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("가입 신청 정보를 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun UserStatus(email: String, status: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            accessionRepository.userStatus(email, status)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    LoadAccession()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("가입 상태를 변경하지 못했습니다.")
                        )
                    }
                }
        }
    }
}

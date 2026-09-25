package com.example.shinhan_qna_aos.servepage.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppealUiState(
    val appeal: AppealData? = null,
    val blockReasonData: BlockReasonData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AppealViewModel(
    private val appealRepository: AppealRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppealUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAppeals(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            appealRepository.appeal()
                .onSuccess { appeal ->
                    _uiState.update { it.copy(appeal = appeal, isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("이의신청 접수에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun loadBlockReason(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            appealRepository.blockReason(email)
                .onSuccess { reason ->
                    _uiState.update { it.copy(blockReasonData = reason, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("차단 사유를 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }
}

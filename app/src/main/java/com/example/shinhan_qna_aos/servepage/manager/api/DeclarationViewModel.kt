package com.example.shinhan_qna_aos.servepage.manager.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeclarationUiState(
    val declarationList: List<DeclarationData> = emptyList(),
    val rejectResult: Boolean? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DeclarationViewModel(private val declarationRepository: DeclarationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DeclarationUiState())
    val uiState = _uiState.asStateFlow()

    fun LoadDeclaration() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            declarationRepository.loadDeclaration()
                .onSuccess { declarations ->
                    _uiState.update { it.copy(declarationList = declarations, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("신고 목록을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun DeclarationReject(reportId: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(rejectResult = null, isLoading = true, errorMessage = null)
            }
            declarationRepository.declarationReject(reportId)
                .onSuccess {
                    _uiState.update { it.copy(rejectResult = true, isLoading = false) }
                    LoadDeclaration()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            rejectResult = false,
                            isLoading = false,
                            errorMessage = error.userMessage("신고 반려에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun resetRejectResult() {
        _uiState.update { it.copy(rejectResult = null) }
    }
}

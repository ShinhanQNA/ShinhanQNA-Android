package com.example.shinhan_qna_aos.info.api

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.ImageUtils
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InfoUiState(
    val form: InfoData = InfoData(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class InfoViewModel(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(InfoUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(form = it.form.copy(name = newName)) }
    }

    fun onStudentIdChange(newId: String) {
        _uiState.update {
            it.copy(form = it.form.copy(students = newId.toIntOrNull() ?: 0))
        }
    }

    fun onGradeChange(newGrade: String) {
        _uiState.update {
            it.copy(
                form = it.form.copy(year = newGrade.removeSuffix("학년").toIntOrNull() ?: 0)
            )
        }
    }

    fun onMajorChange(newMajor: String) {
        _uiState.update { it.copy(form = it.form.copy(department = newMajor)) }
    }

    fun onImageChange(uri: Uri) {
        _uiState.update { it.copy(form = it.form.copy(imageUri = uri)) }
    }

    fun submitStudentInfo(
        context: Context,
        reapplying: Boolean,
        onSuccess: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value
            val compressedFile = ImageUtils.compressImage(context, state.form.imageUri)
            if (compressedFile == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "이미지를 처리하지 못했습니다. 다시 선택해 주세요."
                    )
                }
                return@launch
            }

            infoRepository.submitStudentInfo(state.form, compressedFile)
                .onSuccess {
                    if (reapplying) {
                        infoRepository.requestReapplication()
                            .onSuccess {
                                _uiState.update { it.copy(isLoading = false) }
                                onSuccess(true)
                            }
                            .onFailure { error ->
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = error.userMessage(
                                            "신청 상태 변경에 실패했습니다. 다시 시도해 주세요."
                                        )
                                    )
                                }
                            }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess(false)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage(
                                "학생 정보 제출에 실패했습니다. 다시 시도해 주세요."
                            )
                        )
                    }
                }
        }
    }
}

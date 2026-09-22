package com.example.shinhan_qna_aos.info.api

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InfoViewModel(
    private val infoRepository: InfoRepository
) : ViewModel() {

    // UI 상태를 나타내는 StateFlow, 외부에는 읽기전용으로 노출
    private val _uiState = MutableStateFlow(InfoData())
    var uiState: StateFlow<InfoData> = _uiState.asStateFlow()

    private val _submitError = MutableStateFlow<String?>(null)
    val submitError = _submitError.asStateFlow()


    // 이름 변경 시 호출, infoData 내 name 값 갱신
    fun onNameChange(newName: String) {
        _uiState.value =
            _uiState.value.copy(name = newName)
    }

    // 학번(학생 번호) 변경 시 호출, 문자열을 정수로 변환 후 갱신 (변환 실패 시 0으로 초기화)
    fun onStudentIdChange(newId: String) {
        _uiState.value =
            _uiState.value.copy(students = newId.toIntOrNull() ?: 0)
    }

    // 학년 변경 시 호출, "학년" 접미사 제거 후 정수로 변환하며 기본값 0 처리
    fun onGradeChange(newGrade: String) {
        val gradeInt = newGrade.removeSuffix("학년").toIntOrNull() ?: 0
        _uiState.value =
            _uiState.value.copy(year = gradeInt)
    }

    // 전공(학과) 변경 시 호출, infoData 내 department 값 갱신
    fun onMajorChange(newMajor: String) {
        _uiState.value =
            _uiState.value.copy(department = newMajor)
    }

    // 이미지 변경 시 호출, 단순히 Uri 값만 infoData에 저장
    fun onImageChange(uri: Uri) {
        _uiState.value =
            _uiState.value.copy(imageUri = uri)
    }

    /**
     * 학생 정보 제출 (서버 업로드 후 상태 체크 및 분기)
     */
    fun submitStudentInfo(
        context: Context,
        reapplying: Boolean,
        onSuccess: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _submitError.value = null
            val imageUri = _uiState.value.imageUri
            val compressedFile = ImageUtils.compressImage(context, imageUri)
            if (compressedFile == null) {
                _submitError.value = "이미지를 처리하지 못했습니다. 다시 선택해 주세요."
                return@launch
            }

            val submitResult = infoRepository.submitStudentInfo(_uiState.value, compressedFile)
            if (submitResult.isSuccess) {
                if (reapplying) {
                    if (infoRepository.requestReapplication().isFailure) {
                        _submitError.value = "신청 상태 변경에 실패했습니다. 다시 시도해 주세요."
                        return@launch
                    }
                }
                onSuccess(reapplying)
            } else {
                _submitError.value = "학생 정보 제출에 실패했습니다. 다시 시도해 주세요."
            }
        }
    }
}

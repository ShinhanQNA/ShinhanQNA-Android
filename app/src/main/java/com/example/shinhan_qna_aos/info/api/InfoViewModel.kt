package com.example.shinhan_qna_aos.info.api

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.ImageUtils
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.debugLog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class InfoViewModel(
private val infoRepository: InfoRepository,
private val data: Data
) : ViewModel() {

    // UI 상태를 나타내는 StateFlow, 외부에는 읽기전용으로 노출
    private val _uiState = MutableStateFlow(InfoData())
    var uiState: StateFlow<InfoData> = _uiState.asStateFlow()

    // 네비게이션 경로 상태
    private val _navigationRoute = MutableStateFlow<String?>(null)
    val navigationRoute: StateFlow<String?> = _navigationRoute.asStateFlow()

    private val _submitError = MutableStateFlow<String?>(null)
    val submitError = _submitError.asStateFlow()

    fun beginReapplication() {
        data.isReapplying = true
        _navigationRoute.value = "info"
    }


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
    fun submitStudentInfo(context: Context) {
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
                if (data.isReapplying) {
                    if (infoRepository.requestReapplication().isFailure) {
                        _submitError.value = "신청 상태 변경에 실패했습니다. 다시 시도해 주세요."
                        return@launch
                    }
                    data.isReapplying = false
                    _navigationRoute.value = "wait"
                } else {
                    checkAndNavigateUserStatus()
                }
            } else {
                _submitError.value = "학생 정보 제출에 실패했습니다. 다시 시도해 주세요."
            }
        }
    }

    /**
     * 서버에서 유저 정보를 받아 로컬데이터 갱신 + 네비게이션 경로 상태 업데이트
     */
    fun updateLocalAndNavigate(userResponseWrapper: UserResponseWrapper) {
        val user = userResponseWrapper.user

        // 로컬 데이터 업데이트
        data.userStatus = user.status
        data.userName = user.name
        data.userEmail = user.email
        data.studentCertified = user.studentCertified == true

        if (user.status !in setOf("가입 대기 중", "가입 거절", "거절")) data.isReapplying = false

        // 승인 상태이면 이의신청 완료 상태 리셋
        if (user.status == "가입 완료") {
            resetAppealCompleted()
        }

        // 화면 분기 결정
        val destination = destinationForUserStatus(user.status, user.studentCertified, data.isAppealCompleted, data.isReapplying)

        // 변경 후 (무한 호출 방지)
        if (_navigationRoute.value != destination) {
            _navigationRoute.value = destination
            debugLog("InfoViewModel", "이동할 화면이 변경되었습니다.")

        }
    }

    // 유저 정보 서버 조회 후 상태 갱신 및 네비게이션 분기 함수
    fun checkAndNavigateUserStatus() {
        viewModelScope.launch {
            debugLog("InfoViewModel", "사용자 상태를 확인합니다.")
            val result = infoRepository.checkUserStatus()
            val userResponseWrapper = result.getOrNull()

            if (userResponseWrapper == null) {
                Log.e("InfoViewModel", "사용자 상태 조회에 실패했습니다: 응답이 없습니다.")
                return@launch
            }

            val user = userResponseWrapper.user
            if (user == null) { // 추가: user가 null인 경우 방어 처리
                Log.e("InfoViewModel", "응답에 사용자 데이터가 없습니다.")
                return@launch
            }

            updateLocalAndNavigate(userResponseWrapper)
        }
    }

    // 이의신청 완료 상태 리셋을 위한 함수(승인/재차단 등 필요시 사용)
    fun resetAppealCompleted() {
        data.clearAppealCompleted()
    }

}

internal fun destinationForUserStatus(
    status: String,
    studentCertified: Boolean?,
    appealCompleted: Boolean,
    reapplying: Boolean = false
): String = when {
    reapplying && status in setOf("가입 대기 중", "가입 거절", "거절") -> "info"
    status == "차단" && appealCompleted -> "appeal3"
    status == "차단" -> "appeal1"
    status == "경고" -> "main"
    status == "거절" || status == "가입 거절" -> "refuse"
    status == "가입 완료" -> "main"
    status == "가입 대기 중" -> "wait"
    studentCertified == false -> "info"
    else -> "info"
}

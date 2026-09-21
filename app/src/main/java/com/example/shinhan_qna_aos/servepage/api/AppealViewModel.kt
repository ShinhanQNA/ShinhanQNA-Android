package com.example.shinhan_qna_aos.servepage.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.Data
import kotlinx.coroutines.launch

class AppealViewModel(
    private val appealRepository: AppealRepository,
    private val data: Data
) : ViewModel() {

    // 게시글 목록 상태
    var appeal by mutableStateOf<AppealData?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // 단일 차단 사유 데이터 상태 (null 초기값 허용)
    var blockReasonData by mutableStateOf<BlockReasonData?>(null)
        private set

    // 이의 신청 불러오기
    fun loadAppeals(onSuccess: () -> Unit) {
        viewModelScope.launch {
            errorMessage = null
            appealRepository.appeal()
                .onSuccess { response ->
                    appeal = response
                    data.isAppealCompleted = true
                    onSuccess()
                }
                .onFailure {
                    errorMessage = "이의신청 접수에 실패했습니다."
                    Log.e("AppealViewModel", "이의신청 접수에 실패했습니다.")
                }
        }
    }

    // 해당 이메일로 차단 사유 불러오기
    fun loadBlockReason(email: String) {
        viewModelScope.launch {
            appealRepository.blockReason(email)
                .onSuccess { data ->
                    blockReasonData = data
                }
                .onFailure {
                    Log.e("AppealViewModel", "차단 사유를 불러오지 못했습니다.")
                }
        }
    }
}

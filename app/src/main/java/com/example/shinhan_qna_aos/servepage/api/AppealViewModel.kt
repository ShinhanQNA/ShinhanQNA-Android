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

    // 단일 차단 사유 데이터 상태 (null 초기값 허용)
    var blockReasonData by mutableStateOf<BlockReasonData?>(null)
        private set

    // 이의 신청 불러오기
    fun loadAppeals() {
        viewModelScope.launch {
            appealRepository.appeal()
                .onSuccess { data ->
                    appeal = data // 내가 쓴 게스글 리스트로 받음
                }
                .onFailure { error ->
                    // 에러 처리 (예: 로그 출력)
                    Log.e("loadAppealsError", error.message ?: "Unknown error")
                }
        }
    }

    // 해당 이메일로 차단 사유 불러오기
    fun loadBlockReason(email: String) {
        viewModelScope.launch {
            appealRepository.blockReason(email)
                .onSuccess { data ->
                    blockReasonData = data
                    Log.d("blockReasonData", data.toString())  // 실제 받은 데이터 로그 출력
                }
                .onFailure { error ->
                    Log.e("loadBlockReasonError", error.message ?: "Unknown error")
                }
        }
    }
}
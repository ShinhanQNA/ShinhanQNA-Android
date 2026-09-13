package com.example.shinhan_qna_aos.servepage.manager.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DeclarationViewModel(private val declarationRepository: DeclarationRepository) : ViewModel() {

    var declarationList by mutableStateOf<List<DeclarationData>>(emptyList())

    // 호출 성공 여부 상태 관리
    var rejectResult by mutableStateOf<Boolean?>(null)
        private set

    // 신고된 게시글 조회
    fun LoadDeclaration() {
        viewModelScope.launch {
            declarationRepository.loadDeclaration()
                .onSuccess { declarationList = it }
        }
    }

    // 신고 반려
    fun DeclarationReject(reportId: Int) {
        viewModelScope.launch {
            try {
                declarationRepository.declarationReject(reportId)
                    .onSuccess {
                        LoadDeclaration()
                        rejectResult = true // 성공 처리
                    }
                    .onFailure {
                        Log.e("DeclarationViewModel", "Failed to reject: ${it.message}")
                        rejectResult = false
                    }
            } catch (e: Exception) {
                Log.e("DeclarationViewModel", "Exception in DeclarationReject: ${e.message}")
                rejectResult = false
            }
        }
    }

    // 성공 상태 초기화 함수
    fun resetRejectResult() {
        rejectResult = null
    }
}

package com.example.shinhan_qna_aos.servepage.manager.api

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AccessionViewModel(private val accessionRepository: AccessionRepository ) : ViewModel() {

    var accessionList by mutableStateOf<List<AccessionData>>(emptyList())
    var accessiondetail by mutableStateOf<AccessionDetailData?>(null)

    // 리스트 로드
    fun LoadAccession() {
        viewModelScope.launch {
            accessionRepository.loadAccession()
                .onSuccess {
                    accessionList = it
                }
        }
    }

    // 상세 정보 로드 (이메일 선택)
    fun LoadAccessionDetail(email: String) {
        viewModelScope.launch {
            accessionRepository.loadAccessionDetail(email)
                .onSuccess { accessiondetail=it }
        }
    }

    // 유저 상태 변경
    fun UserStatus(email: String, status: String) {
        viewModelScope.launch {
            accessionRepository.userStatus(email, status)
                .onSuccess { LoadAccession() }
        }
    }
}

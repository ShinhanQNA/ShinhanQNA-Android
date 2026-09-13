package com.example.shinhan_qna_aos.servepage.manager.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BanClearViewModel( private val banClearRepository: BanClearRepository) : ViewModel() {

    // 이의 신청 리스트 변수 저장
    var banClearList by mutableStateOf<List<BanClearData>>(emptyList())
    // 이의 신청 상세 변수 저장
    var banClearDetail by mutableStateOf<BanClearUser?>(null)
    // 이의 신청 상세 게시글 변수 저장
    var banClearPost by mutableStateOf<Board?>(null)

    // 이의 신청 리스트
    fun LoadBanClearList() {
        viewModelScope.launch {
            banClearRepository.loadBanClearList()
            .onSuccess { banClearList = it }
            .onFailure { Log.e("BanClearViewModel",  "신청 리스트 에러: ${it.message}") }
        }
    }

    // 이의 신청 상세
    fun LoadBanClearDetail(email: String) {
        viewModelScope.launch {
            banClearRepository.banClearUser(email)
            .onSuccess { banClearDetail = it }
            .onFailure { Log.e("BanClearViewModel",  "이의 신청 상세 에러: ${it.message}") }
        }
    }

    // 이의 신청 상세 게시글
    fun LoadBanClearPost(email: String, postId: Int) {
        viewModelScope.launch {
            banClearRepository.banUserPost(email, postId)
            .onSuccess { banClearPost = it  }
            .onFailure { Log.e("BanClearViewModel",  "이의 신청 상세 게시글 에러: ${it.message}") }
        }
    }

    // 이의 제기 상태 변경
    fun banStatus(status:String,appealId : Int) {
        viewModelScope.launch {
            banClearRepository.banStatus(status, appealId)
        }
    }
}
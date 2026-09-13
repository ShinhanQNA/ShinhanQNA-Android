package com.example.shinhan_qna_aos.servepage.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.main.api.Answer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

    private val _noticesList = MutableStateFlow<List<Notices>>(emptyList())
    val noticesList = _noticesList.asStateFlow()

    // 현재 선택된 단일 공지 상태
    private val _selectedNotices = MutableStateFlow<Notices?>(null)
    val selectedNotices = _selectedNotices.asStateFlow()

    var noticesState by mutableStateOf(
        UiNoticesRequest(
            id=0,
            title = "",
            content = "",
            editMode = false
        )
    )

    fun onTitleChange(newTitle: String) {
        noticesState = noticesState.copy(title = newTitle)
    }

    fun onContentChange(newContent: String) {
        noticesState = noticesState.copy(content = newContent)
    }

    // 전체 공지 리스트 로드
    fun loadNotification(id: Int? = null) {
        viewModelScope.launch {
            val result = repository.getNotification()
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _noticesList.value = list

                id?.let {
                    val answer = list.find { it.id == id }
                    _selectedNotices.value = answer
                }
            }
        }
    }

    // 공지 작성
    fun noticesWrite(onSusscess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.NoticesWrite(
                noticesState.title,
                noticesState.content
            )
            result
                .onSuccess {
                    Log.d("AnswerViewModel", "답변 작성 성공")
                    onSusscess()
                    loadNotification()
                }
                .onFailure { Log.d("AnswerViewModel", "답변 작성 실패") }
        }
    }

    // 수정 모드 진입
    fun NoticesEditMode(answerRequest: Notices?) {
        noticesState = noticesState.copy(
            id = answerRequest?.id ?: 0,
            title = answerRequest?.title ?: "",
            content = answerRequest?.content ?: "",
            editMode = true
        )
    }
    // 수정 사항 카피
    fun noticesEditMode() {
        noticesState = noticesState.copy(editMode = false)
    }

    // 게시글 수정
    fun updateNotices(
        id: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updateNoticesPost(
                id = id,
                title = noticesState.title,
                content = noticesState.content,
            )
            result
                .onSuccess {
                    noticesEditMode()
                    onSuccess()
                }
                .onFailure { onError() }
        }
    }
    // 삭제
    fun deleteNotices(id: Int) {
        viewModelScope.launch {
            val result = repository.NoticesDelete(id)
            if (result.isSuccess) {
                Log.d("PostViewModel", "삭제 성공")
            } else {
                Log.e("PostViewModel", "삭제 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}

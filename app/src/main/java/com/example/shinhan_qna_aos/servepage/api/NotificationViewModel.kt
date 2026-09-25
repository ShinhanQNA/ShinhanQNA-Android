package com.example.shinhan_qna_aos.servepage.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.debugLog
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationUiState(
    val noticesList: List<Notices> = emptyList(),
    val selectedNotice: Notices? = null,
    val form: UiNoticesRequest = UiNoticesRequest(id = 0, title = "", content = "", editMode = false),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(form = it.form.copy(title = newTitle)) }
    }

    fun onContentChange(newContent: String) {
        _uiState.update { it.copy(form = it.form.copy(content = newContent)) }
    }

    fun loadNotification(id: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getNotification()
                .onSuccess { notices ->
                    _uiState.update {
                        it.copy(
                            noticesList = notices,
                            selectedNotice = id?.let { selectedId ->
                                notices.find { notice -> notice.id == selectedId }
                            } ?: it.selectedNotice,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("공지 목록을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun noticesWrite(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val form = _uiState.value.form
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.NoticesWrite(form.title, form.content)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    debugLog("NotificationViewModel", "공지를 작성했습니다.")
                    onSuccess()
                    loadNotification()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("공지 생성에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun NoticesEditMode(notice: Notices?) {
        _uiState.update {
            it.copy(
                form = it.form.copy(
                    id = notice?.id ?: 0,
                    title = notice?.title ?: "",
                    content = notice?.content ?: "",
                    editMode = true
                )
            )
        }
    }

    fun noticesEditMode() {
        _uiState.update { it.copy(form = it.form.copy(editMode = false)) }
    }

    fun updateNotices(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val form = _uiState.value.form
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.updateNoticesPost(id, form.title, form.content)
                .onSuccess {
                    _uiState.update {
                        it.copy(form = it.form.copy(editMode = false), isLoading = false)
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("공지 수정에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun deleteNotices(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.NoticesDelete(id)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                    debugLog("NotificationViewModel", "공지를 삭제했습니다.")
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("공지 삭제에 실패했습니다.")
                        )
                    }
                }
        }
    }
}

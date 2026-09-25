package com.example.shinhan_qna_aos.main.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.debugLog
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnswerUiState(
    val answerList: List<Answer> = emptyList(),
    val selectedAnswer: Answer? = null,
    val form: UiAnswerRequest = UiAnswerRequest(title = "", content = "", editMode = false),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AnswerViewModel(private val repository: AnswerRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AnswerUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(form = it.form.copy(title = newTitle)) }
    }

    fun onContentChange(newContent: String) {
        _uiState.update { it.copy(form = it.form.copy(content = newContent)) }
    }

    fun loadAnswers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getAnswers()
                .onSuccess { answers ->
                    _uiState.update { it.copy(answerList = answers, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("답변 목록을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun selectAnswerById(id: Int) {
        _uiState.update { state ->
            state.copy(selectedAnswer = state.answerList.find { it.id == id })
        }
    }

    fun writeAnswer(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val form = _uiState.value.form
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.AnswerWrite(form.title, form.content)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    debugLog("AnswerViewModel", "답변을 작성했습니다.")
                    onSuccess()
                    loadAnswers()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("답변 생성에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun AnswerEditMode(answerRequest: Answer?) {
        _uiState.update {
            it.copy(
                form = it.form.copy(
                    title = answerRequest?.title ?: "",
                    content = answerRequest?.content ?: "",
                    editMode = true
                )
            )
        }
    }

    fun answerEditMode() {
        _uiState.update { it.copy(form = it.form.copy(editMode = false)) }
    }

    fun updateAnswerPost(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val form = _uiState.value.form
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.updateAnswerPost(id, form.title, form.content)
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
                            errorMessage = error.userMessage("답변 수정에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun deleteAnswerPost(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.AnswerDelete(id)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                    debugLog("AnswerViewModel", "답변을 삭제했습니다.")
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("답변 삭제에 실패했습니다.")
                        )
                    }
                }
        }
    }
}

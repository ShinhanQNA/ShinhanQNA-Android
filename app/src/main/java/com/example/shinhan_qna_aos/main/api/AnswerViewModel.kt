package com.example.shinhan_qna_aos.main.api

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnswerViewModel(private val repository: AnswerRepository) : ViewModel() {

    private val _answerList = MutableStateFlow<List<Answer>>(emptyList())
    val answerList = _answerList.asStateFlow()

    // 현재 선택된 단일 답변 상태
    private val _selectedAnswer = MutableStateFlow<Answer?>(null)
    val selectedAnswer = _selectedAnswer.asStateFlow()

    var answerstate by mutableStateOf(UiAnswerRequest(
        title = "",
        content = "",
        editMode = false
        )
    )

    // 제목 입력 변경 시 호출
    fun onTitleChange(newTitle: String) {
        answerstate = answerstate.copy(title = newTitle)
    }

    // 내용 입력 변경 시 호출
    fun onContentChange(newContent: String) {
        answerstate = answerstate.copy(content = newContent)
    }

    // 전체 답변 리스트 로드
    fun loadAnswers() {
        viewModelScope.launch {
            val result = repository.getAnswers()
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                _answerList.value = list
            } else {
                // 에러 처리 로직 추가 가능
            }
        }
    }

    // id 기준 단일 답변을 리스트에서 검색하여 선택 상태에 설정
    fun selectAnswerById(id: Int) {
        val answer = _answerList.value.find { it.id == id }
        _selectedAnswer.value = answer
    }

    // 답변 작성
    fun writeAnswer(onSusscess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.AnswerWrite(
                answerstate.title,
                answerstate.content
            )
            result
                .onSuccess {
                    Log.d("AnswerViewModel", "답변 작성 성공")
                    onSusscess()
                    loadAnswers()
                }
                .onFailure { Log.d("AnswerViewModel", "답변 작성 실패") }
        }
    }

    // 수정 모드 진입
    fun AnswerEditMode(answerRequest: Answer?) {
        answerstate = answerstate.copy(
            title = answerRequest?.title ?: "",
            content = answerRequest?.content ?: "",
            editMode = true
        )
    }
    // 수정 사항 카피
    fun answerEditMode() {
        answerstate = answerstate.copy(editMode = false)
    }

    // 게시글 수정
    fun updateAnswerPost(
        id: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updateAnswerPost(
                id = id,
                title = answerstate.title,
                content = answerstate.content,
            )
            result
                .onSuccess {
                    answerEditMode()
                    onSuccess()
                }
                .onFailure { onError() }
        }
    }
    // 삭제
    fun deleteAnswerPost(id: Int) {
        viewModelScope.launch {
            val result = repository.AnswerDelete(id)
            if (result.isSuccess) {
                Log.d("PostViewModel", "삭제 성공")
            } else {
                Log.e("PostViewModel", "삭제 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}

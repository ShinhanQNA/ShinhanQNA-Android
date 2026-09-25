package com.example.shinhan_qna_aos.main.api

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.debugLog
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TWPostUiState(
    val opinions: List<GroupID> = emptyList(),
    val groupDetailList: List<GroupList> = emptyList(),
    val selectedSort: String = "date",
    val selectedYear: Int = 0,
    val selectedMonth: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TWPostViewModel(private val repository: TWPostRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TWPostUiState())
    val uiState = _uiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadOpinions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.fetchThreeWeekOpinions()
                .onSuccess { opinions ->
                    _uiState.update { it.copy(opinions = opinions, isLoading = false) }
                    debugLog("TWPostViewModel", "의견 목록을 불러왔습니다.")
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("의견 목록을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun loadGroupDetailPosts(groupId: Int, sort: String = "date") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.fetchGroupDetail(groupId, sort)
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(
                            groupDetailList = detail.opinions,
                            selectedYear = detail.selectedYear,
                            selectedMonth = detail.selectedMonth,
                            selectedSort = sort,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("그룹 상세 정보를 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun changeSort(groupId: Int, newSort: String) {
        if (newSort != _uiState.value.selectedSort) loadGroupDetailPosts(groupId, newSort)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun GroupStatusPost(groupId: Int, status: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.putStatus(groupId, status)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    loadOpinions()
                    debugLog("TWPostViewModel", "그룹 상태를 변경했습니다.")
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("그룹 상태를 변경하지 못했습니다.")
                        )
                    }
                }
        }
    }
}

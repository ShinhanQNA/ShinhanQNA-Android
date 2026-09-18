package com.example.shinhan_qna_aos.main.api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.debugLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TWPostViewModel(private val repository: TWPostRepository) : ViewModel() {

    // UI에서 관찰할 StateFlow (초기값 빈 리스트)
    private val _opinions = MutableStateFlow<List<GroupID>>(emptyList())
    val opinions: StateFlow<List<GroupID>> = _opinions.asStateFlow()

    // 선택된 그룹 상세 리스트 상태
    private val _groupDetailList = MutableStateFlow<List<GroupList>>(emptyList())
    val groupDetailList = _groupDetailList.asStateFlow()

    // 현재 선택된 정렬 기준 ("date" 또는 "likes")
    private val _selectedSort = MutableStateFlow("date")
    val selectedSort = _selectedSort.asStateFlow()

    // 여기 연도, 월 상태 추가
    private val _selectedYear = MutableStateFlow(0)
    val selectedYear = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(0)
    val selectedMonth = _selectedMonth.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadOpinions() {
        viewModelScope.launch {
            debugLog("TWPostViewModel", "의견 목록을 불러옵니다.")
            val result = repository.fetchThreeWeekOpinions()
            if (result.isSuccess) {
                debugLog("TWPostViewModel", "의견 목록을 불러왔습니다.")
                val opinions = result.getOrNull() ?: emptyList()
                _opinions.value = opinions
            } else {
                Log.e("TWPostViewModel", "의견 목록을 불러오지 못했습니다.")
            }
        }
    }

    // 상세 데이터 로드 시 연도/월도 함께 저장
    fun loadGroupDetailPosts(groupId: Int, sort: String = "date") {
        viewModelScope.launch {
            val result = repository.fetchGroupDetail(groupId, sort)
            if (result.isSuccess) {
                val twPostData = result.getOrNull()
                if (twPostData != null) {
                    debugLog("TWPostViewModel", "그룹 상세 정보를 불러왔습니다: 의견 수=${twPostData.opinions.size}")
                    _groupDetailList.value = twPostData.opinions
                    _selectedYear.value = twPostData.selectedYear
                    _selectedMonth.value = twPostData.selectedMonth
                    _selectedSort.value = sort
                }
            } else {
                Log.e("TWPostViewModel", "그룹 상세 정보를 불러오지 못했습니다.")
            }
        }
    }

    // 정렬 방식 변경 시 호출 (기존과 다르면 API 재호출)
    fun changeSort(groupId: Int, newSort: String) {
        if (newSort != _selectedSort.value) {
            loadGroupDetailPosts(groupId, newSort)
        }
    }

    // 그룹 상태 변경
    @RequiresApi(Build.VERSION_CODES.O)
    fun GroupStatusPost(groupId: Int, status: String) {
        viewModelScope.launch {
            val result = repository.putStatus(groupId, status)
            if (result.isSuccess) {
                loadOpinions()
                debugLog("TWPostViewModel", "그룹 상태를 변경했습니다.")
            } else {
                Log.e("TWPostViewModel", "그룹 상태를 변경하지 못했습니다.")
            }
        }
    }
}

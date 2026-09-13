package com.example.shinhan_qna_aos.main.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PostViewModel(
    private val postRepository: PostRepository,   // API 호출 담당
) : ViewModel() {

    // 게시글 리스트
    var postList by mutableStateOf<List<TitleContentLike>>(emptyList())

    // 내가 쓴 게시글 리스트
    var myPostList by mutableStateOf<List<MyPostData>>(emptyList())
        private set

    var selectedPost by mutableStateOf<PostDetail?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // 좋아요 상태 (해당 게시글에 사용자가 좋아요 눌렀는지)
    var hasLiked by mutableStateOf(false)
        private set

    /**
     * 게시글 목록 로드
     */
    fun loadPosts() {
        viewModelScope.launch {
            postRepository.getPosts() // 나중에 관리자인 경우 sort 선택 가능(day,year,like) 유저는 day
                .onSuccess {
                    postList = it
                }
                .onFailure { errorMessage = it.message }
        }
    }

    /**
     * 게시글 상세 로드
     */
    fun loadPostDetail(postId: String) {
        viewModelScope.launch {
            postRepository.getPostDetail(postId)
                .onSuccess {
                    selectedPost = it
                    Log.d("PostViewModel", "loadPostDetail onSuccess, imagePath: ${it.imagePath}")
                }
                .onFailure {
                    errorMessage = it.message
                    Log.e("PostViewModel", "loadPostDetail error: ${it.message}")
                }
        }
    }

    // 좋아요 토글 함수: 좋아요 <-> 좋아요 취소
    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            try {
                val result = if (!hasLiked) {
                    postRepository.PostLike(postId)
                } else {
                    postRepository.PostUnlike(postId)
                }
                if (result.isSuccess) {
                    hasLiked = !hasLiked
                    loadPostDetail(postId.toString())
                } else {
                    errorMessage = result.exceptionOrNull()?.message
                    Log.e("PostViewModel", "toggleLike 실패: $errorMessage")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "toggleLike 예외: ${e.message}")
            }
        }
    }
    // 신고 관련
    fun flagPost(
        postId: Int,
        reportReason: String?,
        context: Context
    ) {
        viewModelScope.launch {
            postRepository.Postflag(postId, reportReason)
                .onSuccess {
                    loadPostDetail(postId.toString())
                    Toast.makeText(context, "신고 되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .onFailure { errorMessage = it.message }
        }
    }

    // 삭제
    fun deletePost(postId: Int) {
        viewModelScope.launch {
            val result = postRepository.PostDelete(postId)
            if (result.isSuccess) {
                Log.d("PostViewModel", "삭제 성공")
            } else {
                Log.e("PostViewModel", "삭제 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    // 경고/차단
    fun warningUser(email: String, status: String, reason: String, postId: String) {
        viewModelScope.launch {
            postRepository.PostWarning(email, status, reason)
                .onSuccess { warning ->
                    if (warning.message == "이미 경고된 사용자 입니다.") {
                        // 경고 중복 시 차단 재시도
                        warningUser(email, "차단", reason, postId)
                    } else {
                        loadPostDetail(postId)  // 상세 정보 다시 조회
                    }
                }
                .onFailure { error ->
                    errorMessage = error.message
                }
        }
    }

    /**
     * 내가 쓴 게시글 목록 로드
     */
    fun loadMyPosts() {
        viewModelScope.launch {
            postRepository.getMyPosts()
                .onSuccess {
                    myPostList = it
                }
                .onFailure { error ->
                    errorMessage = error.message
                    Log.e("PostViewModel", "내 게시글 조회 실패: ${error.message}")
                }
        }
    }

}
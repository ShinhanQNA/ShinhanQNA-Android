package com.example.shinhan_qna_aos.main.api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.debugLog
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostUiState(
    val postList: List<TitleContentLike> = emptyList(),
    val myPostList: List<MyPostData> = emptyList(),
    val selectedPost: PostDetail? = null,
    val hasLiked: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class PostViewModel(
    private val postRepository: PostRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState = _uiState.asStateFlow()

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            postRepository.getPosts()
                .onSuccess { posts ->
                    _uiState.update { it.copy(postList = posts, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("게시글 목록을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun loadPostDetail(postId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            postRepository.getPostDetail(postId)
                .onSuccess { post ->
                    _uiState.update { it.copy(selectedPost = post, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("게시글 상세 정보를 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            val hasLiked = _uiState.value.hasLiked
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (hasLiked) {
                postRepository.PostUnlike(postId)
            } else {
                postRepository.PostLike(postId)
            }
            result
                .onSuccess {
                    _uiState.update { it.copy(hasLiked = !hasLiked, isLoading = false) }
                    loadPostDetail(postId.toString())
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("공감 상태를 변경하지 못했습니다.")
                        )
                    }
                }
        }
    }

    fun flagPost(postId: Int, reportReason: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            postRepository.Postflag(postId, reportReason)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, successMessage = "신고 되었습니다.") }
                    loadPostDetail(postId.toString())
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("게시글 신고에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun deletePost(postId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            postRepository.PostDelete(postId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                    debugLog("PostViewModel", "게시글을 삭제했습니다.")
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("게시글 삭제에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun warningUser(email: String, status: String, reason: String, postId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            postRepository.PostWarning(email, status, reason)
                .onSuccess { warning ->
                    if (warning.message == "이미 경고된 사용자 입니다.") {
                        warningUser(email, "차단", reason, postId)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                        loadPostDetail(postId)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("사용자 경고 또는 차단에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun loadMyPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            postRepository.getMyPosts()
                .onSuccess { posts ->
                    _uiState.update { it.copy(myPostList = posts, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("내 게시글을 불러오지 못했습니다.")
                        )
                    }
                }
        }
    }
}

package com.example.shinhan_qna_aos.servepage.api

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.ImageUtils
import com.example.shinhan_qna_aos.main.api.PostDetail
import com.example.shinhan_qna_aos.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class WritingUiState(
    val form: WriteData = WriteData(title = "", content = "", imageUri = null, isEditMode = false),
    val compressedImageFile: File? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class WritingViewModel(
    private val writeRepository: WriteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WritingUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(form = it.form.copy(title = newTitle)) }
    }

    fun onContentChange(newContent: String) {
        _uiState.update { it.copy(form = it.form.copy(content = newContent)) }
    }

    fun onImageChange(context: Context, uri: Uri) {
        _uiState.update {
            it.copy(form = it.form.copy(imageUri = uri), isLoading = true, errorMessage = null)
        }
        viewModelScope.launch {
            val file = ImageUtils.compressImage(context, uri)
            _uiState.update {
                it.copy(
                    compressedImageFile = file,
                    isLoading = false
                )
            }
        }
    }

    fun enterEditMode(postDetail: PostDetail, context: Context) {
        _uiState.update {
            it.copy(
                form = it.form.copy(
                    title = postDetail.title,
                    content = postDetail.content,
                    imageUri = postDetail.imagePath?.toUri(),
                    isEditMode = true
                ),
                errorMessage = null
            )
        }
        postDetail.imagePath?.let { imagePath ->
            viewModelScope.launch {
                val file = ImageUtils.compressImage(context, imagePath.toUri())
                _uiState.update { it.copy(compressedImageFile = file) }
            }
        }
    }

    fun exitEditMode() {
        _uiState.update { it.copy(form = it.form.copy(isEditMode = false)) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun uploadPost(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            writeRepository.writeBoards(
                title = state.form.title,
                content = state.form.content,
                imageFile = state.compressedImageFile
            )
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("게시글 작성에 실패했습니다.")
                        )
                    }
                }
        }
    }

    fun updatePost(postId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            writeRepository.updatePost(
                postId = postId,
                title = state.form.title,
                content = state.form.content,
                imageFile = state.compressedImageFile
            )
                .onSuccess {
                    _uiState.update {
                        it.copy(form = it.form.copy(isEditMode = false), isLoading = false)
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage("게시글 수정에 실패했습니다.")
                        )
                    }
                }
        }
    }
}

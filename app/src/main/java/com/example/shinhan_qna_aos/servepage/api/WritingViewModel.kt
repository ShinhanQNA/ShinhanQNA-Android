package com.example.shinhan_qna_aos.servepage.api

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shinhan_qna_aos.ImageUtils
import com.example.shinhan_qna_aos.main.api.PostDetail
import kotlinx.coroutines.launch
import java.io.File

class WritingViewModel(
    private val writeRepository: WriteRepository
) : ViewModel() {

    // 파일 압축 결과 저장 (이미지 파일)
    var compressedImageFile: File? by mutableStateOf(null)
        private set

    // Compose에서 상태 관찰 (제목, 내용, 카테고리, 이미지 URI)
    var state by mutableStateOf(
        WriteData(
            title = "",
            content = "",
            imageUri = null,
            isEditMode = false
        )
    )
        private set

    // 제목 입력 변경 시 호출
    fun onTitleChange(newTitle: String) {
        state = state.copy(title = newTitle)
    }

    // 내용 입력 변경 시 호출
    fun onContentChange(newContent: String) {
        state = state.copy(content = newContent)
    }

    // 이미지 선택 시 호출: URI저장 + 비동기로 압축 파일 저장
    fun onImageChange(context: Context, uri: Uri) {
        state = state.copy(imageUri = uri)
        viewModelScope.launch {
            // 이미지 압축 유틸 호출(압축 결과를 File로 저장)
            compressedImageFile = ImageUtils.compressImage(context, uri)
        }
    }

    // ✅ 수정 모드 진입
    fun enterEditMode(postDetail: PostDetail, context: Context) {
        state = state.copy(
            title = postDetail.title,
            content = postDetail.content,
            imageUri = postDetail.imagePath?.toUri(),
            isEditMode = true
        )
        postDetail.imagePath?.let { imagePath ->
            viewModelScope.launch {
                compressedImageFile = ImageUtils.compressImage(context, imagePath.toUri())
            }
        }
    }

    fun exitEditMode() {
        state = state.copy(isEditMode = false)
    }

    // 게시글 작성
    fun uploadPost(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = writeRepository.writeBoards(
                title = state.title,
                content = state.content,
                imageFile = compressedImageFile // ← 여기서 이미지 압축 전달
            )
            result
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "알 수 없는 오류 발생") }
        }
    }

    // 게시글 수정
    fun updatePost(
        postId: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            val result = writeRepository.updatePost(
                postId = postId,
                title = state.title,
                content = state.content,
                imageFile = compressedImageFile
            )
            result
                .onSuccess {
                    exitEditMode()
                    onSuccess()
                }
                .onFailure { onError() }
        }
    }
}

package com.example.shinhan_qna_aos.servepage.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.main.api.Post
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class WriteRepository(
    private val apiInterface: APIInterface,
    private val data: Data,
) {
    suspend fun writeBoards(
        title: String,
        content: String,
        imageFile: File? // ← File 직접 전달받기
    ): Result<Post> {
        val accessToken = data.accessToken
            ?: return Result.failure(Exception("로그인 토큰이 없습니다."))

        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
            val imagePart = imageFile?.let { file ->
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            }

            val response = apiInterface.uploadPost(
                accessToken = "Bearer $accessToken",
                title = titleBody,
                content = contentBody,
                image = imagePart
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePost(
        postId: String,
        title: String,
        content: String,
        imageFile: File? // 이미지 파일 optional 파라미터 추가
    ): Result<Post> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 토큰이 없습니다."))

        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
            // 이미지가 있을 경우 MultipartBody.Part 생성
            val imagePart = imageFile?.let { file ->
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())?.let { requestFile ->
                    MultipartBody.Part.createFormData("image", file.name ?: "image.jpg", requestFile)
                }
            }

            val response = apiInterface.updatePost(
                accessToken = "Bearer $accessToken",
                postsid = postId.toInt(),
                title = titleBody,
                content = contentBody,
                image = imagePart  // 이미지 Multipart 전송 반영
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("서버 오류: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
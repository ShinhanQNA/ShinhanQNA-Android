package com.example.shinhan_qna_aos.info.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.Data
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class InfoRepository(private val apiInterface: APIInterface, private val data:Data) {

    // 서버로부터 유저 가입 상태 조회 API 호출
    suspend fun checkUserStatus(): Result<UserResponseWrapper> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 결과가 없습니다."))
        return try {
            val response = apiInterface.UserCheck("Bearer $accessToken")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터가 없습니다."))
            } else {
                Result.failure(Exception("서버 오류가 발생했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 서버에 학생 정보를 multipart 폼으로 제출하는 API 호출 (응답: String으로 처리)
    suspend fun submitStudentInfo(infoData: InfoData, imageFile: File): Result<InfoResponse> {
        val accessToken = data.accessToken ?: return Result.failure(Exception("로그인 결과가 없습니다."))
        return try {
            val studentIdPart = infoData.students.toString().toRequestBody("text/plain".toMediaType())
            val yearPart = infoData.year.toString().toRequestBody("text/plain".toMediaType())
            val namePart = infoData.name.toRequestBody("text/plain".toMediaType())
            val departmentPart = infoData.department.toRequestBody("text/plain".toMediaType())
            val rolePart = infoData.role.toRequestBody("text/plain".toMediaType())
            val studentCertifiedPart = infoData.studentCertified.toString().toRequestBody("text/plain".toMediaType())
            val imagePart = imageFile.let {
                val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            val response = apiInterface.InfoStudent(
                accessToken = "Bearer $accessToken",
                students = studentIdPart,
                name = namePart,
                department = departmentPart,
                year = yearPart,
                role = rolePart,
                studentCertified = studentCertifiedPart,
                image = imagePart
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다."))
                }
            } else {
                Result.failure(Exception("서버 오류가 발생했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

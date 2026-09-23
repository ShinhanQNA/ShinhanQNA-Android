package com.example.shinhan_qna_aos.info.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.API.successOrThrow
import com.example.shinhan_qna_aos.Data
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class InfoRepository(private val apiInterface: APIInterface, private val data:Data) {

    suspend fun requestReapplication(): Result<Unit> = apiResult {
        apiInterface.updateOwnStatus(
            bearerHeader(data.accessToken, "로그인 결과가 없습니다."),
            OwnStatusRequest("가입 대기 중")
        ).successOrThrow("가입 재신청에 실패했습니다.")
    }

    // 서버로부터 유저 가입 상태 조회 API 호출
    suspend fun checkUserStatus(): Result<UserResponseWrapper> = apiResult {
        apiInterface.UserCheck(bearerHeader(data.accessToken, "로그인 결과가 없습니다."))
            .bodyOrThrow()
    }

    // 서버에 학생 정보를 multipart 폼으로 제출하는 API 호출 (응답: String으로 처리)
    suspend fun submitStudentInfo(infoData: InfoData, imageFile: File): Result<InfoResponse> =
        apiResult {
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
                accessToken = bearerHeader(data.accessToken, "로그인 결과가 없습니다."),
                students = studentIdPart,
                name = namePart,
                department = departmentPart,
                year = yearPart,
                role = rolePart,
                studentCertified = studentCertifiedPart,
                image = imagePart
            )
            response.bodyOrThrow()
        }
}

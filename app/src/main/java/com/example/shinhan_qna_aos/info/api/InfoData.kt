package com.example.shinhan_qna_aos.info.api

import android.net.Uri
import com.google.gson.annotations.SerializedName

//API 학생 정보 받음
data class InfoResponse(
    val message:String,
)

//API 학생 정보를 multipart 폼으로 제출
data class InfoData(
    val students: Int = 0,        // 학번
    val name: String = "",         // 이름
    val department: String = "",   // 학과
    val year: Int = 0,             // 학년
    val role: String = "학생",     // 역할
    val studentCertified: Boolean = true,
    val imageUri: Uri = Uri.EMPTY  // 이미지 Uri
)

//API 유저 조회로 받음
data class UserResponseWrapper(
    val user: UserCheckResponse,
    val warnings: List<Warning>?
)

data class UserCheckResponse(
    val email: String? = "",
    val name: String,
    val token: String? = "",
    val role: String,
    val year: String,                // 서버 응답이 문자열이므로 String 타입 유지
    val department: String,
    val studentCardImagePath: String? = null,
    val students: String,            // 서버 응답이 문자열이므로 String 타입 유지
    val status: String,
    @SerializedName("studentCertified")
    val studentCertified: Boolean? = null
)

data class Warning(
    val warningId: Int,
    val email: String,
    val reason: String,
    val warningDate: String,
    val status: String
)
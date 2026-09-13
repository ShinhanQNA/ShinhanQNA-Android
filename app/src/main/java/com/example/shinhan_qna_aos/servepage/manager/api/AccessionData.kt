package com.example.shinhan_qna_aos.servepage.manager.api

import com.google.gson.annotations.SerializedName

//가입 대기 중 리스트
data class AccessionData(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("students") val students: String, // studentid
    @SerializedName("year") val year: String,
    @SerializedName("department") val department: String
)

//가입 대기 중 특정 사용자 상세 조회
data class AccessionDetailData(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("students") val students: String, // studentid
    @SerializedName("year") val year: String,
    @SerializedName("department") val department: String,
    @SerializedName("imagePath") val imagePath: String
)

// 유저 가입 상태 변경
data class UserStatusRequest(
    @SerializedName("email") val email: String,
    @SerializedName("status") val status: String // 가입 완료, 가입 거절
)
// 유저 가입 상태
data class AccessionUserState(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("token") val token : String,
    @SerializedName("role") val role: String,
    @SerializedName("year") val year: String,
    @SerializedName("department") val department: String,
    @SerializedName("studentCardImagePath") val studentCardImagePath: String,
    @SerializedName("students") val students: String, // studentid
    @SerializedName("status") val status: String,
)
package com.example.shinhan_qna_aos.servepage.api

import com.google.gson.annotations.SerializedName

// 내가 쓴 글 모아보기
data class AppealData(
    val id: Int,
    val email: String,
    val createdAt : String,
    val status: String,
)

// 차단 이유 조회 이메일 보내주기
data class ReasonRequest(
    val email: String
)

// 차단 이유 조회 받기
data class BlockReasonData(
    @SerializedName("blockReasons") val blockReasons: List<String>,
    @SerializedName("email") val email: String
)
package com.example.shinhan_qna_aos.servepage.manager.api

import com.google.gson.annotations.SerializedName

data class DeclarationData (
    @SerializedName("reportId")val reportId: Int,
    @SerializedName("postId")val postId: Int,
    @SerializedName("reporterEmail") val reporterEmail: String,
    @SerializedName("reportReason") val reportReason: String,
    @SerializedName("reportDate") val reportDate: String,
    @SerializedName("resolved") val resolved: Boolean
)

data class DeclarationUIModel(
    val postID: Int,
    val reportId: Int,
    val title: String,
    val content: String,
    val likeCount: Int,
    val flagsCount: Int, // 신고 횟수
    val banCount: Int,   // 경고/밴 횟수
    val status: String   // 게시글 상태
)

data class DeclarationRequest(
    @SerializedName("reportId")val reportId: Int
)

data class DeclarationResponse(
    @SerializedName("message")val message: String
)
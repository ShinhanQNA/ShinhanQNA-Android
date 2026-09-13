package com.example.shinhan_qna_aos.servepage.manager.api

import com.google.gson.annotations.SerializedName

// 이의 제기 신청자 조회
data class BanClearData (
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("students") val students: String,
    @SerializedName("year") val year: String,
    @SerializedName("department") val department: String,
    @SerializedName("createdAt") val createdAt: String,
)

// 이의 제기 신청자 상세조회
data class BanClearUser(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("students") val students: String,
    @SerializedName("year") val year: String,
    @SerializedName("department") val department: String,
    @SerializedName("boards") val boards: List<Board>
)

// 이의 제기 신청자 개별 게시글
data class Board(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("likes") val likes: Int,
    @SerializedName("date") val date: String,
    @SerializedName("status") val status: String,
    @SerializedName("reportCount") val reportCount: Int,
    @SerializedName("warningStatus") val warningStatus: String,
    @SerializedName("writerEmail") val writerEmail: String,
    @SerializedName("imagePath") val imagePath: String?
)

// 이의 제기 상태 변경
data class BanClearStatus(
    @SerializedName("status") val status : String //기본:대기 -> 승인 OR 거절
)

data class BanClearStatusResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("status") val status: String
)
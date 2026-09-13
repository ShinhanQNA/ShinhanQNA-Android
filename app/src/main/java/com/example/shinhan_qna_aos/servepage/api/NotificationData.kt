package com.example.shinhan_qna_aos.servepage.api

data class Notices(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)

data class NoticesRequest(
    val title: String,
    val content: String
)

data class UiNoticesRequest(
    val id: Int,
    val title: String,
    val content: String,
    val editMode: Boolean
)
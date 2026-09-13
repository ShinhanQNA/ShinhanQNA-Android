package com.example.shinhan_qna_aos.main.api

data class Answer(
    val id: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)

data class AnswerRequest(
    val title: String,
    val content: String
)

data class UiAnswerRequest(
    val title: String,
    val content: String,
    val editMode: Boolean
)
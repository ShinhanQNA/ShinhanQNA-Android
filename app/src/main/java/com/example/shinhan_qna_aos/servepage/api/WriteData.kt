package com.example.shinhan_qna_aos.servepage.api

import android.net.Uri

// 글쓰기 ui 용
data class WriteData(
    val title: String,
    val content: String,
    val imageUri: Uri?,
    val isEditMode: Boolean
)

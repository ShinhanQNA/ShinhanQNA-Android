package com.example.shinhan_qna_aos.main.api

import com.google.gson.annotations.SerializedName

data class TWPostData(
    val id : Int,
    @SerializedName("selectedYear") val selectedYear: Int,
    @SerializedName("selectedMonth") val selectedMonth: Int,
    val responseStatus: String,
    val createdAt:String,
    val opinions: List<GroupList>
)

data class GroupList(
    val id : Int,
    val postId: Int,
    val title: String,
    val content: String,
    val likes: Int,
    val date: String,
    val imagePath: String?,
    val createdAt: String
)

data class GroupID(
    @SerializedName("selectedMonth") val selectedMonth: Int,
    @SerializedName("groupId") val groupId: Int,
    @SerializedName("responseStatus") val responseStatus: String, // 완료, 응답 대기
)

data class GroupStatus(
    @SerializedName("status") val status: String,
)

data class GroupStatusRequest(
   val status: String,
)
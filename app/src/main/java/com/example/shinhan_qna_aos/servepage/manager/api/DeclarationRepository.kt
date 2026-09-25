package com.example.shinhan_qna_aos.servepage.manager.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.Data

class DeclarationRepository (
    private val data:Data,
    private val apiInterface: APIInterface
){

    // 신고 당한 게시물 불러오기
    suspend fun loadDeclaration():Result<List<DeclarationData>> = apiResult {
        apiInterface.Declaration(bearerHeader(data.accessToken)).bodyOrThrow()
    }

    // 신고 게시글 반려
    suspend fun declarationReject(reportId: Int): Result<DeclarationResponse> = apiResult {
        apiInterface.DeclarationReject(
            bearerHeader(data.accessToken),
            DeclarationRequest(reportId)
        ).bodyOrThrow()
    }
}

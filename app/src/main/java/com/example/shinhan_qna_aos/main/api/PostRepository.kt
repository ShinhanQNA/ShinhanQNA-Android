package com.example.shinhan_qna_aos.main.api

import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.API.apiResult
import com.example.shinhan_qna_aos.API.bearerHeader
import com.example.shinhan_qna_aos.API.bodyOrThrow
import com.example.shinhan_qna_aos.API.successOrThrow
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.main.warningStatusToBanCount

class PostRepository(
    private val apiInterface: APIInterface,
    private val data: Data
) {

    /**
     * 게시글 목록 조회
     * @param size 가져올 개수
     * @param sort 정렬 방식 (예: "day")
     */
    suspend fun getPosts(): Result<List<TitleContentLike>> = apiResult {
        apiInterface.getPosts(bearerHeader(data.accessToken)).bodyOrThrow().map {
            TitleContentLike(
                postID = it.postID,
                title = it.title,
                content = it.content,
                likeCount = it.likes,
                flagsCount = it.reportCount,
                banCount = warningStatusToBanCount(it.warningStatus),
                responseState = it.status
            )
        }
    }

    /**
     * 게시글 상세 조회
     */
    suspend fun getPostDetail(postId: String): Result<PostDetail> = apiResult {
        apiInterface.getPostsDetail(bearerHeader(data.accessToken), postId)
            .bodyOrThrow("상세 데이터 없음")
    }

    /**
     * 게시글 좋아요 취소
     */
    suspend fun PostUnlike(postId: Int): Result<PostLike> = apiResult {
        apiInterface.PostUnlike(bearerHeader(data.accessToken), postId)
            .bodyOrThrow("좋아요 취소 실패")
    }

    /**
     * 게시글 좋아요
     */
    suspend fun PostLike(postId: Int): Result<PostLike> = apiResult {
        val response = apiInterface.PostLike(bearerHeader(data.accessToken), postId)
        if (response.code() == 400 &&
            response.errorBody()?.string().orEmpty().contains("이미 공감한 게시글")
        ) {
            return@apiResult PostUnlike(postId).getOrThrow()
        }
        response.bodyOrThrow("공감 처리 응답이 없습니다.")
    }

    /**
     * 게시글 신고
     */
    suspend fun Postflag(postId: Int, reportReason: String?): Result<PostFlag> = apiResult {
        apiInterface.PostFlag(
            accessToken = bearerHeader(data.accessToken),
            postId = postId,
            reportReasonBody = ReportReasonBody(reportReason)
        ).bodyOrThrow("신고 실패")
    }
    /**
     * 게시글 취소
     */
    suspend fun PostDelete(postId: Int): Result<Unit> = apiResult {
        apiInterface.PostDelete(bearerHeader(data.accessToken), postId).successOrThrow()
    }

    /**
     * 관리자 게시글 사용자 신고 및 차단
     */
    suspend fun PostWarning(email: String, status: String, reason: String): Result<Warning> = apiResult {
        apiInterface.UserWarning(
            bearerHeader(data.accessToken),
            WarningRequest(email, status, reason)
        ).bodyOrThrow("응답이 비었습니다")
    }

    /**
     * 나의 게시글 목록 조회
     */
    suspend fun getMyPosts(): Result<List<MyPostData>> = apiResult {
        apiInterface.MyPost(bearerHeader(data.accessToken)).bodyOrThrow()
    }
}

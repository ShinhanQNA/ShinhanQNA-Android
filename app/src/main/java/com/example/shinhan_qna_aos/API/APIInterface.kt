package com.example.shinhan_qna_aos.API

import com.example.shinhan_qna_aos.info.api.InfoResponse
import com.example.shinhan_qna_aos.info.api.UserResponseWrapper
import com.example.shinhan_qna_aos.login.api.AdminRequest
import com.example.shinhan_qna_aos.login.api.LoginTokensResponse
import com.example.shinhan_qna_aos.login.api.LogoutData
import com.example.shinhan_qna_aos.login.api.RefreshTokenRequest
import com.example.shinhan_qna_aos.main.api.Answer
import com.example.shinhan_qna_aos.main.api.AnswerRequest
import com.example.shinhan_qna_aos.main.api.GroupID
import com.example.shinhan_qna_aos.main.api.GroupStatus
import com.example.shinhan_qna_aos.main.api.GroupStatusRequest
import com.example.shinhan_qna_aos.main.api.MyPostData
import com.example.shinhan_qna_aos.main.api.Post
import com.example.shinhan_qna_aos.main.api.PostData
import com.example.shinhan_qna_aos.main.api.PostDetail
import com.example.shinhan_qna_aos.main.api.PostFlag
import com.example.shinhan_qna_aos.main.api.PostLike
import com.example.shinhan_qna_aos.main.api.ReportReasonBody
import com.example.shinhan_qna_aos.main.api.TWPostData
import com.example.shinhan_qna_aos.main.api.Warning
import com.example.shinhan_qna_aos.main.api.WarningRequest
import com.example.shinhan_qna_aos.servepage.api.AppealData
import com.example.shinhan_qna_aos.servepage.api.BlockReasonData
import com.example.shinhan_qna_aos.servepage.api.Notices
import com.example.shinhan_qna_aos.servepage.api.NoticesRequest
import com.example.shinhan_qna_aos.servepage.api.ReasonRequest
import com.example.shinhan_qna_aos.servepage.manager.api.AccessionData
import com.example.shinhan_qna_aos.servepage.manager.api.AccessionDetailData
import com.example.shinhan_qna_aos.servepage.manager.api.AccessionUserState
import com.example.shinhan_qna_aos.servepage.manager.api.BanClearData
import com.example.shinhan_qna_aos.servepage.manager.api.BanClearStatus
import com.example.shinhan_qna_aos.servepage.manager.api.BanClearStatusResponse
import com.example.shinhan_qna_aos.servepage.manager.api.BanClearUser
import com.example.shinhan_qna_aos.servepage.manager.api.Board
import com.example.shinhan_qna_aos.servepage.manager.api.DeclarationData
import com.example.shinhan_qna_aos.servepage.manager.api.DeclarationRequest
import com.example.shinhan_qna_aos.servepage.manager.api.DeclarationResponse
import com.example.shinhan_qna_aos.servepage.manager.api.UserStatusRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface APIInterface {
    //kakao
    @Headers("Content-Type: application/json")
    @POST("/oauth/callback/kakao")
    suspend fun KakaoAuthCode(
        @Header("Authorization") accessToken: String
    ): Response<LoginTokensResponse>

    //구글
    @Headers("Content-Type: application/json")
    @POST("/oauth/callback/google")
    suspend fun GoogleAuthCode(
        @Header("Authorization") code : String
    ): Response<LoginTokensResponse>

    // 토큰 재발급
    @POST("/token/reissue")
    suspend fun ReToken(
        @Body refreshTokenRequest: RefreshTokenRequest
    ): Response<LoginTokensResponse>

    // 관리자 로그인
    @POST("/admin/login")
    suspend fun AdminLoginData(
        @Body adminRequest : AdminRequest
    ):Response<LoginTokensResponse>

    // 로그아웃
    @POST("/users/logout")
    suspend fun LogOut(
        @Header("Authorization") refreshToken: String,
    ): Response<LogoutData>

    // 학생 정보
    @Multipart
    @POST("/users/certify")
    suspend fun InfoStudent(
        @Header("Authorization") accessToken: String,
        @Part("students") students: RequestBody,
        @Part("name") name: RequestBody,
        @Part("department") department: RequestBody,
        @Part("year") year: RequestBody,
        @Part("role") role: RequestBody,
        @Part("studentCertified") studentCertified : RequestBody,
        @Part image: MultipartBody.Part
    ): Response<InfoResponse>

    //유저 정보 조회
    @GET("/users/me")
    suspend fun UserCheck(
        @Header("Authorization") accessToken: String
    ): Response<UserResponseWrapper>

    //게시글 조회
    @Headers("Content-Type: application/json")
    @GET("/boards")
    suspend fun getPosts(
        @Header("Authorization") code : String
    ):Response<List<PostData>>

    //게시글 상세조회
    @Headers("Content-Type: application/json")
    @GET("/boards/{postsid}")
    suspend fun getPostsDetail(
        @Header("Authorization") accessToken: String,
        @Path("postsid") postsid: String?
    ):Response<PostDetail>

    // 게시글 작성
    @Multipart
    @POST("/boards")
    suspend fun uploadPost(
        @Header("Authorization") accessToken: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part image: MultipartBody.Part?
    ):Response<Post>

    // 게시글 수정
    @Multipart
    @PUT("/boards/{postsid}")
    suspend fun updatePost(
        @Header("Authorization") accessToken: String,
        @Path("postsid") postsid: Int,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Post>

    // 게시글 공감
    @POST("/boards/{postId}/like")
    suspend fun PostLike(
        @Header("Authorization") accessToken: String,
        @Path("postId") postId: Int,  // postId로 수정 (URL 변수명과 같아야 함)
    ): Response<PostLike>

    // 게시글 공감 취소
    @POST("/boards/{postId}/unlike")
    suspend fun PostUnlike(
        @Header("Authorization") accessToken: String,
        @Path("postId") postId: Int,
    ): Response<PostLike>

    // 게시글 신고
    @POST("/boards/{postId}/report")
    suspend fun PostFlag(
        @Header("Authorization") accessToken: String,
        @Path("postId") postId: Int,
        @Body reportReasonBody: ReportReasonBody   // 신고 이유 -> 없어도 됨
    ): Response<PostFlag>

    // 게시글 삭제
    @DELETE("/boards/{postsid}")
    suspend fun PostDelete(
        @Header("Authorization") accessToken: String,
        @Path("postsid") postsid: Int,
    ): Response<Unit>

    // 3주 그룹 조회
    @GET("/three-week-opinions/groups/ids")
    suspend fun ThreeWeekPost(
        @Header("Authorization") accessToken: String,
        @Query ("year") year :Int,
    ): Response<List<GroupID>>

    // 3주 그룹 데이터로 상세 조회
    @GET("/three-week-opinions/group/{groupId}")
    suspend fun ThreeWeekPostDetail(
        @Header("Authorization") accessToken: String,
        @Path("groupId") groupId: Int,
        @Query("sort") sort : String // data, likes
    ): Response<TWPostData>

    // 게시글 상태 변경 (3주 조회 대기/응답)
    @PUT("/three-week-opinions/group/{groupId}/status")
    suspend fun ThreeWeekStatus(
        @Header("Authorization") accessToken: String,
        @Path("groupId") groupId: Int,
        @Body groupStatusRequest: GroupStatusRequest,
    ): Response<GroupStatus>

    // 내가 쓴 게시글 조회
    @GET("/boards/my")
    suspend fun MyPost(
        @Header("Authorization") accessToken: String,
    ): Response<List<MyPostData>>

    //답변 조회
    @GET("/answers")
    suspend fun AnswerPost(
        @Header("Authorization") accessToken: String,
    ): Response<List<Answer>>

    //공지 조회
    @GET("/notices")
    suspend fun Notification(
        @Header("Authorization") accessToken: String,
    ): Response<List<Notices>>

    //회원 탈퇴
    @DELETE("/users/me")
    suspend fun CancelMember(
        @Header("Authorization") accessToken: String,
    ): Response<LogoutData>

    // 이의 제기
    @POST("/appeals")
    suspend fun Appeal(
        @Header("Authorization") accessToken: String,
    ): Response<AppealData>

    // 사용자 차단 이유 조회
    @POST("admin/users/block-reasons")
    suspend fun BlockReason(
        @Header("Authorization") accessToken: String,
        @Body reasonRequest: ReasonRequest
    ): Response<BlockReasonData>
    /**
     * 관리자 관련
     */

    //관리자 게시글 경고/차단 권한
    @POST("/admin/users/warning")
    suspend fun UserWarning(
        @Header("Authorization") accessToken: String,
        @Body warningRequest: WarningRequest
    ): Response<Warning>

    // 답변 작성
    @POST("/answers")
    suspend fun AnswerWritePost(
        @Header("Authorization") accessToken: String,
        @Body answerRequest: AnswerRequest
    ): Response<Answer>

    // 답변 수정
    @PUT("/answers/{id}")
    suspend fun UpdateAnswerPost(
        @Header("Authorization") accessToken: String,
        @Body answerRequest: AnswerRequest,
        @Path("id") id: Int // 확실하지 않음 명세서랑 다름
    ): Response<Answer>

    // 답변 삭제
    @DELETE("/answers/{id}")
    suspend fun DeleteAnswerPost(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Response<Unit>

    // 공지 작성
    @POST("/notices")
    suspend fun NoticesWritePost(
        @Header("Authorization") accessToken: String,
        @Body noticesRequest: NoticesRequest
    ): Response<Notices>

    // 공지 수정
    @PUT("/notices/{id}")
    suspend fun UpdateNoticesPost(
        @Header("Authorization") accessToken: String,
        @Body noticesRequest: NoticesRequest,
        @Path("id") id: Int // 확실하지 않음 명세서랑 다름
    ): Response<Notices>

    // 공지 삭제
    @DELETE("/notices/{id}")
    suspend fun DeleteNoticesPost(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Response<Unit>

    // 신고 검토
    @GET("/admin/boards/reports")
    suspend fun Declaration(
        @Header("Authorization") accessToken: String,
    ):Response<List<DeclarationData>>

    // 신고 게시글 반려
    @HTTP(method = "DELETE", path = "/admin/boards/reports/reject", hasBody = true)
    suspend fun DeclarationReject(
        @Header("Authorization") accessToken: String,
        @Body declarationRequest: DeclarationRequest
    ): Response<DeclarationResponse>

    // 가입 대기 검토
    @GET("/admin/pending")
    suspend fun Accession(
        @Header("Authorization") accessToken: String,
    ):Response<List<AccessionData>>

    // 가입 대기 검토 상세
    @GET("/admin/pending/{email}")
    suspend fun AccessionDetail(
        @Header("Authorization") accessToken: String,
        @Path("email") email: String,
    ): Response<AccessionDetailData>

    // 유저 가입 상태 변경
    @PUT("/admin/users/status")
    suspend fun AdminUserStatus(
        @Header("Authorization") accessToken: String,
        @Body userStatusRequest: UserStatusRequest,
    ): Response<AccessionUserState>

    // 이의 제기 신청자 리스트
    @GET("/admin/appeals")
    suspend fun BanClear(
        @Header("Authorization") accessToken: String,
    ):Response<List<BanClearData>>

    // 이의 제기 신청자 상세 조회
    @GET("/admin/appeals/{email}")
    suspend fun BanClearDetail(
        @Header("Authorization") accessToken: String,
        @Path("email") email: String,
    ): Response<BanClearUser>

    // 이의 제기 신청자 개별 게시글
    @GET("/admin/appeals/{email}/boards/{postId}")
    suspend fun BanClearDetailBoard(
        @Header("Authorization") accessToken: String,
        @Path("email") email: String,
        @Path("postId") postId: Int,
    ): Response<Board>

    //이의 제기 상태 변경
    @PUT("/admin/appeals/{appealId}/status")
    suspend fun BanClearStatus(
        @Header("Authorization") accessToken: String,
        @Path("appealId") appealId: Int,
        @Body banClearStatus: BanClearStatus
    ):Response<BanClearStatusResponse>
}


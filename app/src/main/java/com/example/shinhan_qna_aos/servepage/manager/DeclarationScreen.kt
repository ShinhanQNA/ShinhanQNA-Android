package com.example.shinhan_qna_aos.servepage.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shinhan_qna_aos.DetailContent
import com.example.shinhan_qna_aos.ManagerButton
import com.example.shinhan_qna_aos.TopBar
import com.jihan.lucide_icons.lucide
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.PlainInputField
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TitleContentCountButton
import com.example.shinhan_qna_aos.TitleContentLikeButton
import com.example.shinhan_qna_aos.main.api.PostRepository
import com.example.shinhan_qna_aos.main.api.PostViewModel
import com.example.shinhan_qna_aos.servepage.manager.api.DeclarationRepository
import com.example.shinhan_qna_aos.servepage.manager.api.DeclarationUIModel
import com.example.shinhan_qna_aos.servepage.manager.api.DeclarationViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard

@Composable
fun DeclarationScreen(
    declarationRepository: DeclarationRepository,
    postRepository: PostRepository,
    data: Data,
    navController: NavController
) {
    val declarationViewModel: DeclarationViewModel = viewModel(factory = SimpleViewModelFactory {DeclarationViewModel(declarationRepository)})
    val postViewModel: PostViewModel = viewModel(factory = SimpleViewModelFactory {PostViewModel(postRepository)})

    val declarationList = declarationViewModel.declarationList
    val postList by remember { derivedStateOf { postViewModel.postList } }

    LaunchedEffect(Unit) {
        declarationViewModel.LoadDeclaration()
        postViewModel.loadPosts()
    }

    val combinedList = remember(declarationList, postList) {
        declarationList.mapNotNull { decl ->
            val post = postList.find { it.postID == decl.postId }
            post?.let {
                DeclarationUIModel(
                    postID = it.postID,
                    reportId = decl.reportId,
                    title = it.title,
                    content = it.content,
                    likeCount = it.likeCount,
                    flagsCount = it.flagsCount,
                    banCount = it.banCount.toIntOrNull() ?: 0,
                    status = it.responseState
                )
            }
        }
    }

    Box {
        Column {
            TopBar("신고 검토", onNavigationClick = {navController.popBackStack()})

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(bottom = 50.dp)
            ) {
                items(combinedList) { item ->
                    TitleContentCountButton(
                        title = item.title,
                        content = item.content,
                        likeCount = item.likeCount,
                        isAdmin = data.isAdmin,
                        flagsCount = item.flagsCount,
                        banCount = item.banCount,
                        onClick = { navController.navigate("declaration/${item.postID}/${item.reportId}") }
                    )
                    Divider()
                }
            }
        }
        Text(
            "배너광고",
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Red)
                .align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeclarationOpenScreen(postId: String, reportId: Int, navController: NavController, postRepository: PostRepository,declarationRepository: DeclarationRepository) {

    val postViewModel: PostViewModel = viewModel(factory = SimpleViewModelFactory {PostViewModel(postRepository)})
    val declarationViewModel: DeclarationViewModel = viewModel(factory = SimpleViewModelFactory {DeclarationViewModel(declarationRepository)})

    var showSheet by remember { mutableStateOf(false) } // 사유 작성
    var reason by remember { mutableStateOf("") } // 사유

    // 첫 진입시 상세 데이터 불러오기
    LaunchedEffect(postId) {
        postViewModel.loadPostDetail(postId)
    }

    val postDetail = postViewModel.selectedPost
    val writerEmail = postDetail?.writerEmail ?: ""

    // API 호출 결과 감지하여 네비게이션 처리
    val rejectResult = declarationViewModel.rejectResult
    LaunchedEffect(rejectResult) {
        if (rejectResult == true) {
            navController.popBackStack()
            declarationViewModel.resetRejectResult()
        }
    }

    Column(modifier = Modifier.systemBarsPadding().fillMaxSize().background(Color.White)) {
        TopBar(null) { navController.navigate("declaration") { popUpTo("declaration/${postId}") { inclusive = true } } }
        LazyColumn() {
            item {
                DetailContent(
                    title = postDetail?.title.toString(),
                    content = postDetail?.content ?: ""
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ManagerButton(
                        icon = lucide.arrow_big_left_dash,
                        label = "반려",
                        background = Color(0xffFC4F4F),
                        onClick = {
                            declarationViewModel.DeclarationReject(reportId)
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    ManagerButton(
                        icon = lucide.flag,
                        label = "경고",
                        background = Color(0xffFF9F43),
                        onClick = {showSheet=true}
                    )
                }
            }
        }
    }
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false }, // 시트가 닫히도록 요청될 때 (바깥 클릭, 뒤로가기)
            containerColor = Color.White,
        ) {
            Column() {
                Text(
                    "사유 작성",
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(20.dp)
                )
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    if (reason.isNullOrEmpty()) {
                        Text(
                            text = "차단 사유는 사용자에게 제공됩니다.\n자세하게 기제해주세요.",
                            color = Color(0xffDFDFDF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = pretendard,
                            modifier = Modifier.align(Alignment.TopStart)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                    PlainInputField(
                        value = reason,
                        onValueChange = { reason = it },
                        fontSize = 14.sp,
                        modifier = Modifier
                            .height(200.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .background(Color(0xffFC4F4F), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { showSheet = false }  //취소 버튼
                    ) {
                        Icon(
                            painter = painterResource(lucide.x),
                            contentDescription = "취소",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Text(
                            text = "취소",
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 두 번째 버튼: 확인
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .background(Color(0xff4AD871), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable {
                                postViewModel.warningUser(
                                    email = writerEmail,
                                    status = "경고",
                                    reason = reason,
                                    postId = postId
                                )
                                reason=""
                                showSheet = false
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_check),
                            contentDescription = "확인",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Text(
                            text = "확인",
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeclarationPreview(){
//    DeclarationScreen()
}
package com.example.shinhan_qna_aos.servepage.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.DetailContent
import com.example.shinhan_qna_aos.LikeFlagBan
import com.example.shinhan_qna_aos.ManagerStudentInfo
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TitleContentCountButton
import com.example.shinhan_qna_aos.TitleYearButton
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.main.warningStatusToBanCount
import com.example.shinhan_qna_aos.servepage.manager.api.BanClearRepository
import com.example.shinhan_qna_aos.servepage.manager.api.BanClearViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide

// 이의 제기
@Composable
fun BanClearScreen(
    banClearRepository: BanClearRepository,
    navController: NavController
) {
    val banClearViewModel: BanClearViewModel = viewModel(factory = SimpleViewModelFactory { BanClearViewModel(banClearRepository) })

    LaunchedEffect(Unit) {
        banClearViewModel.LoadBanClearList()
    }

    val banClearList = banClearViewModel.banClearList

    Box (modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
        .background(Color.White)
    ){
        Column {
            TopBar("이의 제기 검토", { navController.popBackStack() })
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(bottom = 50.dp)
            ) {
                items(banClearList) { banClearList ->
                    TitleYearButton(
                        name = banClearList.name,
                        studentid = banClearList.students,
                        major = banClearList.department,
                        grade = banClearList.year,
                        onClick = {
                            navController.navigate("banclearDetail/${banClearList.email}")
                        }
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

@Composable
fun BanClearDetailScreen(
    banClearRepository: BanClearRepository,
    navController: NavController,
    email : String
) {
    val banClearViewModel: BanClearViewModel =
        viewModel(factory = SimpleViewModelFactory { BanClearViewModel(banClearRepository) })

    LaunchedEffect(email) {
        banClearViewModel.LoadBanClearDetail(email)
    }

    val banClearDetail = banClearViewModel.banClearDetail


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = 25.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar("", { navController.popBackStack() }) // 타이틀 없을 땐 공백
            ManagerStudentInfo(
                "이름",
                banClearDetail?.name ?: "",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp) // 좌우 간격 유지
                    .fillMaxWidth(), // Row가 화면 전체를 차지하게
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ManagerStudentInfo(
                    "학번",
                    banClearDetail?.students ?: "",
                    modifier = Modifier.weight(0.7f)
                )
                ManagerStudentInfo(
                    "학년",
                    banClearDetail?.year ?: "",
                    modifier = Modifier.weight(0.3f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            ManagerStudentInfo(
                "학과",
                banClearDetail?.department ?: "",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn() {
                banClearDetail?.let {
                    items(it.boards) { boarddata ->
                        TitleContentCountButton(
                            title = boarddata.title,
                            content = boarddata.content,
                            likeCount = boarddata.likes,
                            isAdmin = true,
                            flagsCount = boarddata.reportCount,
                            banCount = warningStatusToBanCount(boarddata.warningStatus).toInt(),
                            onClick = { navController.navigate("banclearPost/${boarddata.writerEmail}/${boarddata.postId}") }
                        )
                        Divider()
                    }
                }
            }
        }
        // 하단 버튼 바닥 고정
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .align(Alignment.BottomCenter),   // 바닥에 고정
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(Color(0xffFC4F4F), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable {
                        banClearViewModel.banStatus("거절", banClearDetail!!.id)
                        navController.popBackStack()
                    }
            ) {
                Icon(
                    painter = painterResource(lucide.x),
                    contentDescription = "거절",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Text(
                    text = "거절",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                )
            }

            Spacer(Modifier.width(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(Color(0xff4AD871), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable {
                        banClearViewModel.banStatus("승인", banClearDetail!!.id)
                        navController.popBackStack()
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_check),
                    contentDescription = "승인",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Text(
                    text = "승인",
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

@Composable
fun BanClearPostScreen(banClearRepository: BanClearRepository, navController: NavController, email : String, postId : Int, data: Data){
    val banClearViewModel: BanClearViewModel = viewModel(factory = SimpleViewModelFactory { BanClearViewModel(banClearRepository) })

    LaunchedEffect(email, postId) {
        banClearViewModel.LoadBanClearPost(email, postId)
    }
    val post = banClearViewModel.banClearPost

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopBar("", {navController.popBackStack()}) // 타이틀 없을 땐 공백
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn() {
            item{
                post?.let { DetailContent(it.title, post.content, post.imagePath) }
                Spacer(modifier = Modifier.height(16.dp))
                post?.let {
                    LikeFlagBan(
                        it.likes,
                        post.reportCount,
                        warningStatusToBanCount(post.warningStatus).toInt(),
                        data
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BanClearPreview(){
//    BanClearScreen()
//    BanClearDetailScreen()
}
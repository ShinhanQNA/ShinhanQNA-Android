package com.example.shinhan_qna_aos.servepage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.shinhan_qna_aos.ManagerEditDeleteButton
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TitleContentButton
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.servepage.api.NotificationRepository
import com.example.shinhan_qna_aos.servepage.api.NotificationViewModel
import com.example.shinhan_qna_aos.servepage.manager.NoticesEditPostContent
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide

@Composable
fun NotificationScreen(data: Data, notificationRepository: NotificationRepository, navController: NavController) {

    val notificationViewModel:NotificationViewModel =
        viewModel(factory = SimpleViewModelFactory { NotificationViewModel(notificationRepository) })

    LaunchedEffect (Unit){
        notificationViewModel.loadNotification()
    }

    val noticesList by notificationViewModel.noticesList.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .systemBarsPadding()
            .background(Color.White)
    ) {
        TopBar("공지", { navController.navigate("main?selectedTab=0") {popUpTo("notices"){inclusive=true} }})
        Box {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(bottom = 50.dp)
                    .background(Color.White)
            ){
                items(noticesList) { noticeslist ->
                    TitleContentButton(
                        title = noticeslist.title,
                        content = noticeslist.content,
                        onClick = { navController.navigate("notices/${noticeslist.id}")}
                    )
                    Divider()
                }
            }

            if(data.isAdmin){// + 새공지 버튼 - 배너 바로 위 공간에 위치하도록 아래 패딩 추가
                Button(
                    onClick = { navController.navigate("notices_write") },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 66.dp, end = 20.dp) // 배너 위 공간 + 여백 확보
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(lucide.plus),
                            contentDescription = "새공지 추가",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "새공지",
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }

            // 하단 배너 광고
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
}

@Composable
fun NotificationOpenScreen(id:Int,data: Data ,notificationRepository: NotificationRepository, navController: NavController) {
    val notificationViewModel: NotificationViewModel =
        viewModel(factory = SimpleViewModelFactory { NotificationViewModel(notificationRepository) })

    // 공지 리스트가 로드될 때 id와 함께 선택하도록 변경
    LaunchedEffect(Unit) {
        notificationViewModel.loadNotification(id)
    }

    val selectedNotices by notificationViewModel.selectedNotices.collectAsState()
    val uiState = notificationViewModel.noticesState

    Column(modifier = Modifier.systemBarsPadding().fillMaxSize().background(Color.White)) {
        TopBar(if (uiState.editMode) "게시글 수정" else null) {
            if (uiState.editMode) {
                navController.navigate("notices/${id}")
            } else {
                navController.navigate("notices") {
                    popUpTo("notices/${id}") { inclusive = true }
                }
            }
        }
        if (uiState.editMode) {
            NoticesEditPostContent(
                uiState = uiState,
                notificationViewModel = notificationViewModel,
                id = id.toString(),
                navController = navController
            )
        } else {
            LazyColumn() {
                item {
                    DetailContent(
                        title = selectedNotices?.title ?: "",
                        content = selectedNotices?.content ?: ""
                    )
                    if (data.isAdmin) {
                        ManagerEditDeleteButton(
                            onDeleteClick = {
                                notificationViewModel.deleteNotices(id)
                                navController.popBackStack()
                            },
                            onEditClick = {
                                    notificationViewModel.NoticesEditMode(selectedNotices)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationOpenScreenPreview(){
//    NotificationOpenScreen()
}

@Preview(showBackground = true)
@Composable
fun NotificationPreview(){
//    NotificationScreen()
}
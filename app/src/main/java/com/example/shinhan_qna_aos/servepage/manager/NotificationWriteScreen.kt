package com.example.shinhan_qna_aos.servepage.manager//package com.example.shinhan_qna_aos.etc.manager

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.servepage.WriteInfo
import com.example.shinhan_qna_aos.servepage.WritingContentField
import com.example.shinhan_qna_aos.servepage.WritingTitleField
import com.example.shinhan_qna_aos.servepage.api.NotificationRepository
import com.example.shinhan_qna_aos.servepage.api.NotificationViewModel
import com.example.shinhan_qna_aos.servepage.api.UiNoticesRequest
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide

@Composable
fun NotificationWriteScreen(notificationRepository: NotificationRepository,navController: NavController) {
    val notificationViewModel: NotificationViewModel =
        viewModel(factory = SimpleViewModelFactory { NotificationViewModel(notificationRepository) })

    val noticesstate = notificationViewModel.noticesState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White)
            .imePadding() // 키보드에 반응
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            TopBar("공지 작성", { navController.popBackStack() })

            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WritingTitleField(
                        value = noticesstate.title,
                        onValueChange = notificationViewModel::onTitleChange,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    WritingContentField(
                        value = noticesstate.content,
                        onValueChange = notificationViewModel::onContentChange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        WriteInfo()
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd) // 화면 오른쪽 하단 고정
                .padding(20.dp)             // FAB 기본 여백 느낌
                .background(Color.Black, RoundedCornerShape(12.dp))
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .clickable {
                    notificationViewModel.noticesWrite(
                        onSusscess = {
                            navController.navigate("notices") {
                                popUpTo("notification_write") { inclusive = true }
                            }
                        }
                    )

                }
        ) {
            Icon(
                painter = painterResource(lucide.cloud_upload),
                contentDescription = "작성하기",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Text(
                text = "작성하기",
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

@Composable
fun NoticesEditPostContent(
    uiState: UiNoticesRequest,
    notificationViewModel: NotificationViewModel,
    id: String,
    navController: NavController,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        Column {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WritingTitleField(
                        value = uiState.title,
                        onValueChange = notificationViewModel::onTitleChange,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    WritingContentField(
                        value = uiState.content,
                        onValueChange = notificationViewModel::onContentChange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                item {
                    WriteInfo()
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        //  수정 완료 버튼
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .clickable {
                    notificationViewModel.updateNotices(
                        id = id,
                        onSuccess = {
                            notificationViewModel.loadNotification(id.toInt()) // 상세 조회 로드
                            notificationViewModel.loadNotification() // 전체 조회 로드
                            navController.navigate("notices/$id")
                        },
                        onError = {
//                            Toast.makeText(context, "답변 게시글 수정 실패", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
        ) {
            Icon(
                painter = painterResource(lucide.cloud_upload),
                contentDescription = "작성하기",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Text("작성하기", color = Color.White, fontSize = 14.sp)
        }
    }
}
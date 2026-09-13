package com.example.shinhan_qna_aos.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.info.api.InfoRepository
import com.example.shinhan_qna_aos.info.api.InfoViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import kotlinx.coroutines.delay

// 대기 화면
@Composable
fun WaitScreen(infoRepository: InfoRepository, data: Data, navController: NavController) {
    val infoViewModel: InfoViewModel = viewModel(factory = SimpleViewModelFactory { InfoViewModel(infoRepository, data) })

    val navigationRoute by infoViewModel.navigationRoute.collectAsState()

    LaunchedEffect(navigationRoute) {
        navigationRoute?.let { route ->
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (route.isNotBlank() && currentRoute != route) {
                navController.navigate(route) {
                    popUpTo("wait") { inclusive = true }
                }
            }
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()  // 상태바+내비게이션 영역 침범 방지
            .background(Color.White)
    ) {
        val maxwidth = maxWidth
        val maxheight = maxHeight

        // 이미지 크기 화면 너비의 35%, 최소/최대 크기 제한
        val imageSize = (maxwidth * 0.35f).coerceIn(128.dp, 200.dp)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.biglogo),
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "가입 요청 완료",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "[${data.userName}]님의 가입 신청 내용을 안전하게 전달했어요.",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "관리자 확인까지 잠시 시간이 걸릴 수 있습니다.",
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun WaitPreview(){
//    WaitScreen()
}
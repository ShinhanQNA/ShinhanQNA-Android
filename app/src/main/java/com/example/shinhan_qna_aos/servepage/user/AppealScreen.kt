package com.example.shinhan_qna_aos.servepage.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.shinhan_qna_aos.servepage.api.AppealRepository
import com.example.shinhan_qna_aos.servepage.api.AppealViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide

@Composable
fun AppealScreen1(appealRepository: AppealRepository,infoRepository: InfoRepository,data: Data, navController: NavController) {
    val appealViewModel: AppealViewModel = viewModel(factory = SimpleViewModelFactory { AppealViewModel(appealRepository,data) })

    // userEmail 안전하게 가져오기 (기본값 or 안내 메시지 할당)
    val userEmail = data.userEmail ?: ""

    // email이 빈 문자열이면 호출 안함 → 기본값, 에러 처리 등 옵션 선택
    LaunchedEffect(userEmail) {
        if (userEmail.isNotBlank()) {
            appealViewModel.loadBlockReason(userEmail)
        }
    }
    // ViewModel에서 받은 단일 차단 사유 데이터
    val blockReasonList = appealViewModel.blockReasonData?.blockReasons ?: emptyList()

    Column(
        modifier = Modifier.fillMaxSize()
        .padding(horizontal = 20.dp)
        .systemBarsPadding()  // 상태바+내비게이션 영역 침범 방지
        .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.biglogo),
            contentDescription = null,
            modifier = Modifier.size(128.dp)
        )
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "서비스 이용 제한 안내",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = buildString {
                append("[${data.userName}]님은 다음 사유로 인해 서비스 이용이 영구적으로 정지되었음을 알려드립니다.\n\n")
                // 뒤에서 2개 요소만 취함
                blockReasonList.takeLast(2).forEachIndexed { index, reason ->
                    append("${index + 1}. $reason\n")
                }
            },
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "이 결정에 따라 회원님은 더 이상 본 계정으로 로그인하거나 서비스를 이용할 수 없습니다.",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(36.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .background(Color(0xffFC4F4F), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable { navController.navigate("appeal2") },
        ) {
            Icon(
                painter = painterResource(lucide.user),
                contentDescription = "이의 신청하기",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Text(
                text = "이의 신청하기",
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

@Composable
fun AppealScreen2(appealRepository: AppealRepository, data: Data, navController: NavController) {

    val appealViewModel: AppealViewModel = viewModel(factory = SimpleViewModelFactory { AppealViewModel(appealRepository,data) })

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 20.dp)
            .systemBarsPadding()  // 상태바+내비게이션 영역 침범 방지
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.biglogo),
            contentDescription = null,
            modifier = Modifier.size(128.dp)
        )
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "⚠ 중요 안내",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "이의 제기 신청은 마지막 기회입니다. 제출 이후에는 내용 수정이나 재신청이 절대 불가능합니다.",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 13.5.sp
            ),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "저희는 제출된 내용을 바탕으로 신중하게 재검토할 것이며, 이 과정에서 내려진 결정은 번복되지 않는 최종 조치임을 알려드립니다.",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 13.5.sp
            ),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(36.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable {
                    appealViewModel.loadAppeals()
                    data.isAppealCompleted = true
                    navController.navigate("appeal3")
                   },
        ) {
            Icon(
                painter = painterResource(lucide.plus),
                contentDescription = "이의 접수하기",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Text(
                text = "이의 접수하기",
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

@Composable
fun AppealScreen3(infoRepository: InfoRepository, data: Data, navController: NavController) {
    val infoViewModel: InfoViewModel = viewModel(factory = SimpleViewModelFactory { InfoViewModel(infoRepository, data)})

   // 현재 네비게이션 경로를 StateFlow에서 수집
    val navigationRoute by infoViewModel.navigationRoute.collectAsState()

    // 승인, 재차단 등 상태 변화가 감지되면 자동 분기
    LaunchedEffect(navigationRoute) {
        navigationRoute?.let { route ->
            // 현재 라우트와 다르다면 해당 화면으로 이동(예: main, appeal1 등)
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (route.isNotBlank() && currentRoute != route) {
                navController.navigate(route) {
                    popUpTo("appeal3") { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 20.dp)
            .systemBarsPadding()  // 상태바+내비게이션 영역 침범 방지
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.biglogo),
            contentDescription = null,
            modifier = Modifier.size(128.dp)
        )
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "이의 접수 완료",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "[${data.userName}]님의 이의 제기 신청이 완료되었습니다.",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "운영팀에서 신속하게 검토를 진행할 수 있도록 하겠습니다.",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "검토가 진행되는 동안에는 서비스 이용이 불가능하며, 이번 검토를 통해 내려진 결정은 최종적인 효력을 가집니다.",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "기다려 주셔서 감사합니다",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AppealPreview(){
//    AppealScreen1()
//    AppealScreen2()
//    AppealScreen3()
}
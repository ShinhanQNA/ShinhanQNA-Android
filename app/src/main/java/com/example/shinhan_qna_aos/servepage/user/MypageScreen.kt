package com.example.shinhan_qna_aos.servepage.user

import androidx.compose.foundation.Image
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
import com.example.shinhan_qna_aos.Caution
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.login.api.AuthRepository
import com.example.shinhan_qna_aos.login.api.LoginResult
import com.example.shinhan_qna_aos.login.api.LoginViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide

@Composable
fun MypageScreen(authRepository: AuthRepository, data: Data, navController: NavController){
    val loginViewModel: LoginViewModel =
        viewModel(factory = SimpleViewModelFactory { LoginViewModel(authRepository,data) })

    val loginResult by loginViewModel.loginResult.collectAsState()

    // 로그인 상태 관찰 후, Idle 이거나 실패 상태면 로그인 화면으로 이동
    LaunchedEffect(loginResult) {
        when (loginResult) {
            is LoginResult.Idle, is LoginResult.Failure -> {
                navController.navigate("login") {
                    // 현재 스택 모두 제거하여 뒤로가기 방지
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> { /* 로그인 상태일 때는 그냥 유지 */ }
        }
    }
    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)
                .background(Color.White)
        ) {
            TopBar("마이페이지", { navController.navigate("main?selectedTab=0") {popUpTo("myPage"){inclusive=true} }})
            MypageButton(
                onLogoutClick = { loginViewModel.logout() },
                onMyWriteClick = { navController.navigate("my_page_write") },
                onCancleMember = { loginViewModel.CancleMemeber() }
            )
            Spacer(modifier = Modifier.height(48.dp))
            InApp()
            Spacer(modifier = Modifier.weight(1f))
            Caution()
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
fun MypageButton(onLogoutClick:() -> Unit,onMyWriteClick:() -> Unit ,onCancleMember:() -> Unit = {}){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 35.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clickable { onMyWriteClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "내가 작성한 게시글",
                color = Color.Black,
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
            )
            Icon(
                painter = painterResource(lucide.arrow_right),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onMyWriteClick() }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clickable {onLogoutClick() },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                "로그아웃",
                color = Color.Black,
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clickable { onCancleMember() },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                "회원 탈퇴",
                color = Color(0xffFC4F4F),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}

@Composable
fun InApp(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.coffee_inapp),
            contentDescription = "인앱 결제 커피 후원",
            modifier = Modifier.clickable { }
        )
        Image(
            painter = painterResource(R.drawable.ad_inapp),
            contentDescription = "인앱 결제 광고제거",
            modifier = Modifier.clickable { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MypagePreview(){
//    MypageScreen()
}
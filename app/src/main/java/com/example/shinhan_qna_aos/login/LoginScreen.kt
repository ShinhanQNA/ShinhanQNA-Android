package com.example.shinhan_qna_aos.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.BuildConfig
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.login.api.AuthRepository
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.info.api.InfoRepository
import com.example.shinhan_qna_aos.login.api.LoginResult
import com.example.shinhan_qna_aos.login.api.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.map

@Composable
fun LoginScreen(authrepository: AuthRepository ,data: Data, navController: NavController) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(factory = SimpleViewModelFactory { LoginViewModel(authrepository,data) })
    val loginResult by loginViewModel.loginResult.collectAsState()

    // 로그인 성공 시 학생 인증 상태 및 유저 상태에 따른 화면 분기 처리
    LaunchedEffect(loginResult) {
        if (loginResult is LoginResult.Success) {
            Log.d("LoginScreen", "Login success detected")
            if (data.isAdmin){
                navController.navigate("main"){
                    popUpTo("login") { inclusive = true }
                }
            }
            if (data.studentCertified) {
                // 이미 가입 요청한 경우, 로컬 상태 기반으로 네비게이션 분기
                val destination = when (data.userStatus) {
                    "가입 완료" -> "main"
                    "가입 대기 중" -> "wait"
                    else -> "info"
                }
                Log.d("LoginScreen", "Navigating to $destination based on local stored data")
                navController.navigate(destination) {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                // 학생 인증이 되어있지 않은 경우 info 화면으로 이동
                Log.d("LoginScreen", "User not certified, navigating to info")
                navController.navigate("info") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    val googleSignInLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val authCode = account?.serverAuthCode
                if (!authCode.isNullOrEmpty()) {
                    // ViewModel로 Authorization Code 전달
                    loginViewModel.sendGoogleAuthCodeToServer(authCode)
                }
            } catch (e: Exception) {
                Log.e("LoginScreen", "Google login failed: ${e.localizedMessage}", e)
            }
        }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White)
            .padding(top = 64.dp, start = 40.dp, end = 40.dp, bottom = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxw= maxWidth
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.biglogo),
                contentDescription = null,
                modifier = Modifier.size(128.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 카카오 로그인 버튼
                Image(
                    painter = painterResource(R.drawable.kakao_login),
                    contentDescription = "카카오 로그인",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { loginViewModel.loginWithKakao(context) }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 구글 로그인 버튼
                Image(
                    painter = painterResource(R.drawable.google_login),
                    contentDescription = "구글 로그인",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestServerAuthCode(BuildConfig.GOOGLE_WEB_CLIENT_ID) // 웹 클라이언트 ID
                                    .requestEmail()
                                    .build()
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "관리자 전용 페이지",
                    color = Color(0xffDFDFDF),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.clickable { navController.navigate("manager_login") }
                )
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun loginpreview(){
//    val navController = rememberNavController()
//    LoginScreen(navController = navController)
}
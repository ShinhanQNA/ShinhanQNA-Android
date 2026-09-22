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
import com.example.shinhan_qna_aos.BuildConfig
import com.example.shinhan_qna_aos.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onKakaoLogin: (android.content.Context) -> Unit,
    onGoogleLogin: (String) -> Unit,
    onManagerLogin: () -> Unit
) {
    val context = LocalContext.current

    val googleSignInLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val authCode = account?.serverAuthCode
                if (!authCode.isNullOrEmpty()) {
                    onGoogleLogin(authCode)
                }
            } catch (e: Exception) {
                Log.e("LoginScreen", "Google 로그인에 실패했습니다.")
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
                        .clickable { onKakaoLogin(context) }
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
                    modifier = Modifier.clickable(onClick = onManagerLogin)
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

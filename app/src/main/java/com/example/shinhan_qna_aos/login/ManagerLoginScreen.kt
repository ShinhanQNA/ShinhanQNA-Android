package com.example.shinhan_qna_aos.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.login.api.AuthRepository
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.LabeledField
import com.example.shinhan_qna_aos.PlainInputField
import com.example.shinhan_qna_aos.login.api.LoginResult
import com.example.shinhan_qna_aos.login.api.ManagerLoginViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard

@Composable
fun ManagerLoginScreen(
    authRepository: AuthRepository,
    navController: NavController,         // 네비게이션 컨트롤러 추가
    data: Data                    // 로그인 관리자 상태 저장소 추가
) {
    val viewModel: ManagerLoginViewModel = viewModel(factory = SimpleViewModelFactory { ManagerLoginViewModel(authRepository) })
    val loginResult by viewModel.loginResult.collectAsState()

    // 로그인 성공 시 즉시 화면 전환 처리 (관리자이므로 main 화면으로 이동)
    LaunchedEffect(loginResult) {
        if (loginResult is LoginResult.Success) {
           if( data.isAdmin ) {
               navController.navigate("main") {
                   popUpTo("manager_login") { inclusive = true }
               }
           }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding() // 상태바+내비게이션 영역 침범 방지
            .background(Color.White)
    ) {
        val screenWidth = maxWidth

        Column (
            modifier = Modifier.fillMaxSize().padding(horizontal = 58.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ManagerId(
                value = viewModel.state.managerId,
                onValueChange = viewModel::onAdminIdChange,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            ManagerPassword(
                value = viewModel.state.managerPassword,
                onValueChange = viewModel::onAdminPasswordChange,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(36.dp))
            ManagerLogin(modifier = Modifier, onClick = { viewModel.login() })
        }
    }
}

// 아이디
@Composable
fun ManagerId(value: String, onValueChange: (String) -> Unit, fontSize: TextUnit) =
    LabeledField("아이디", fontSize) {
        PlainInputField(value, onValueChange, fontSize)
    }


// 비밀번호
@Composable
fun ManagerPassword(value: String, onValueChange: (String) -> Unit, fontSize: TextUnit) =
    LabeledField("비밀번호", fontSize) {
        PlainInputField(value, onValueChange, fontSize, keyboardType = KeyboardType.Password)
    }

// 로그인 버튼
@Composable
fun ManagerLogin(modifier: Modifier = Modifier, onClick:() -> Unit){
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ){
        Box (modifier = Modifier
            .background(color = Color.Black, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .clickable { onClick() } // ✅ 클릭 이벤트 연결
        ){
            Text(
                text = "로그인",
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
@Preview(showBackground = true)
fun Managerpreview(){
//    val viewModel : ManagerLoginViewModel = viewModel()
//    ManagerLogin(viewModel)
}
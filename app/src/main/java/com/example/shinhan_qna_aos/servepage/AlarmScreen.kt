package com.example.shinhan_qna_aos.servepage

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide

@Composable
fun AlarmScreen (navController: NavController){
    Column(modifier = Modifier.systemBarsPadding().fillMaxSize().background(Color.White)) {
        TopBar("알림",{ navController.navigate("main?selectedTab=0") {popUpTo("alarm"){inclusive=true} }})
        Box(){
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize().padding(bottom = 50.dp)) {
                items(6) { data ->
                    AlarmButton()
                    Divider()
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
}

@Composable
fun AlarmButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xffEDEDED), shape = CircleShape)
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.bell_ring),
                contentDescription = "알림 벨",
            )
        }
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "공지가 등록 됐어요!",
                color = Color.Black,
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "2025년 03년 20일",
                color = Color(0xffA5A5A5),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(lucide.x),
            contentDescription = "알림 벨",
            tint = Color(0xffDFDFDF)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmPreview(){
    val navController = NavController(LocalContext.current)
    AlarmScreen(navController)
//    AlarmButton()
}
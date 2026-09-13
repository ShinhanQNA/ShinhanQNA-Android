package com.example.shinhan_qna_aos.servepage.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shinhan_qna_aos.ManagerStudentInfo
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TitleYearButton
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.servepage.manager.api.AccessionRepository
import com.example.shinhan_qna_aos.servepage.manager.api.AccessionViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide

// 가입 요청 검토
@Composable
fun AccessionScreen(
    accessionRepository: AccessionRepository,
    navController: NavController
) {
    val accessionViewModel: AccessionViewModel = viewModel(factory = SimpleViewModelFactory { AccessionViewModel(accessionRepository) })

    LaunchedEffect(Unit) {
        accessionViewModel.LoadAccession()
    }

    val accessionList = accessionViewModel.accessionList

    Box (modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
        .background(Color.White)
    ){
        Column {
            TopBar("가입 요청 검토", { navController.popBackStack() })
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(bottom = 50.dp)
            ) {
                items(accessionList) { accessionData ->
                    TitleYearButton(
                        name = accessionData.name,
                        studentid = accessionData.students,
                        major = accessionData.department,
                        grade = accessionData.year,
                        onClick = {
                            navController.navigate("accessionDetail/${accessionData.email}")
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
fun AccessionDetailScreen(
    accessionRepository: AccessionRepository,
    navController: NavController,
    email: String
) {
    val accessionViewModel: AccessionViewModel =
        viewModel(factory = SimpleViewModelFactory { AccessionViewModel(accessionRepository) })
    val accessionDetail = accessionViewModel.accessiondetail

    // 상세 데이터 이메일로 불러오기
    LaunchedEffect(email) {
        accessionViewModel.LoadAccessionDetail(email)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = 25.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopBar("", { navController.popBackStack() }) // 타이틀 없을 땐 공백
            ManagerStudentInfo(
                "이름",
                accessionDetail?.name ?: "",
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
                    accessionDetail?.students ?: "",
                    modifier = Modifier.weight(0.7f)
                )
                ManagerStudentInfo(
                    "학년",
                    accessionDetail?.year ?: "",
                    modifier = Modifier.weight(0.3f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            ManagerStudentInfo("학과", "소프트웨어융합", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "재학 확인서 첨부(학생증, 재학증명서)",
                style = TextStyle(
                    color = Color.Black,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp) // 좌우 간격 유지
            ) {
                item {
                    if (accessionDetail?.imagePath?.isNotEmpty() == true) {
                        AsyncImage(
                            model = accessionDetail.imagePath,
                            contentDescription = "첨부 이미지",
                            modifier = Modifier.width(300.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .align(Alignment.BottomCenter),   // 바닥에 고정
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    accessionViewModel.UserStatus(email, "가입 거절")
                    navController.popBackStack()
                },
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(Color(0xffFC4F4F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(lucide.x),
                        contentDescription = "거절",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "거절",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    accessionViewModel.UserStatus(email, "가입 완료")
                    navController.popBackStack()
                },
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(Color(0xff4AD871)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "승인",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Text(
                        "승인",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccessionPreview(){
//    AccessionScreen()
//    AccessionDetailScreen()
}
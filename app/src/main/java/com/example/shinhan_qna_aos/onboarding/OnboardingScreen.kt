package com.example.shinhan_qna_aos.onboarding

import com.example.shinhan_qna_aos.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.info.api.InfoViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    data: Data
) {
    val pagerState = rememberPagerState(initialPage = 0)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        val maxW = maxWidth
        val maxH = maxHeight

        val selectedWidth = maxW * 0.15f
        val unselectedWidth = maxW * 0.06f
        val dotHeight = maxW * 0.013f
        val dotPadding = maxW * 0.01f
        val indicatorHeight = maxW * 0.1f

        Column(modifier = Modifier.fillMaxSize()) {

            HorizontalPager(
                count = 2,
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> OnboardingPage1(
                        maxW = maxW,
                        maxH = maxH
                    )
                    1 -> OnboardingPage2(
                        maxW = maxW,
                        maxH = maxH,
                        onFinish = {
                            data.onboarding = false
                            navController.navigate("login") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(indicatorHeight)
                    .background(Color.White),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(dotPadding)
                            .size(
                                width = if (isSelected) selectedWidth else unselectedWidth,
                                height = dotHeight
                            )
                            .background(
                                color = if (isSelected) Color(0xff00A5C2) else Color(0xffD9D9D9),
                                shape = RoundedCornerShape(maxW * 0.02f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPage1(maxW: Dp, maxH: Dp) {
    // 텍스트 크기 비율을 maxW에 따라 동적으로 계산
    val titleFontSize = maxW.value * 0.045f.sp // 16.sp, 18.sp를 대체
    val bodyFontSize = maxW.value * 0.035f.sp // 15.sp를 대체
    val disclaimerFontSize = maxW.value * 0.024f.sp // 9.sp를 대체

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = maxW * 0.1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "작은 의견도 편하게 ",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = titleFontSize * 0.9f // 비율 조정
                    ),
                )
                Text(
                    "익명의 게시글",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = titleFontSize
                    ),
                )
                Text(
                    "로 남겨주세요.",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = titleFontSize * 0.9f
                    ),
                )
            }
            Spacer(modifier = Modifier.height(maxH * 0.03f))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "교수님",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = titleFontSize
                    ),
                )
                Text(
                    "과",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = titleFontSize * 0.9f
                    ),
                )
                Text(
                    "학교",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = titleFontSize * 0.9f
                    ),
                )
                Text(
                    "가 함께 귀 기울이고,",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = titleFontSize * 0.9f
                    ),
                )
            }
            Spacer(modifier = Modifier.height(maxH * 0.005f))
            Text(
                "여러분의 질문이나 건의에 대해 정성스럽게 답해드려요",
                color = Color.Black,
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = bodyFontSize
                ),
                lineHeight = bodyFontSize * 1.8f
            )
            Spacer(modifier = Modifier.height(maxH * 0.13f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(maxH*0.012f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "관리자가 게시글을 관리하고 있으니, 모두가 존중받을 수 있도록 깨끗하고 예의 바르게",
                    color = Color(0xffA5A5A5),
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = disclaimerFontSize
                    ),
                )
                Row {
                    Text(
                        "작성해 주세요. ",
                        color = Color(0xffA5A5A5),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = disclaimerFontSize
                        ),
                    )
                    Text(
                        "부적절한 게시글",
                        color = Color(0xffA5A5A5),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = disclaimerFontSize
                        ),
                    )
                    Text(
                        "을 작성할 경우 경고 또는 차단될 수 있습니다.",
                        color = Color(0xffA5A5A5),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = disclaimerFontSize
                        ),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(maxH * 0.01f))
        Image(
            painter = painterResource(R.drawable.android_mockup1),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OnboardingPage2(
    maxW: Dp,
    maxH: Dp,
    onFinish: () -> Unit
) {
    // 텍스트 크기 비율을 maxW에 따라 동적으로 계산
    val buttonTextSize = maxW.value * 0.04f.sp // 14.sp를 대체
    val bodyTextSize = maxW.value * 0.04f.sp // 17.sp, 19.sp를 대체

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = maxW * 0.05f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = maxW * 0.05f, start = maxW * 0.05f, end = maxW * 0.05f
                ),
            contentAlignment = Alignment.TopEnd
        ) {
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(Color.White),
                modifier = Modifier
                    .border(1.dp, Color(0xffDFDFDF), RoundedCornerShape(12.dp))
                    .size(maxW * 0.2f, maxH * 0.05f)
            ) {
                Text(
                    "확인",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = buttonTextSize
                    )
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(verticalArrangement = Arrangement.spacedBy(maxH * 0.015f)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = maxW * 0.1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "질문에 ",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = bodyTextSize
                    ),
                )
                Text(
                    "답변",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = bodyTextSize * 1.2f // 비율 조정
                    ),
                )
                Text(
                    "이나 ",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = bodyTextSize
                    ),
                )
                Text(
                    "공지사항",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = bodyTextSize * 1.2f
                    ),
                )
                Text(
                    "이 올라오면",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = bodyTextSize
                    ),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = maxW * 0.1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "푸쉬 알림",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = bodyTextSize * 1.2f
                    ),
                )
                Text(
                    "으로 바로 알려드려, 더욱 ",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = bodyTextSize
                    ),
                )
                Text(
                    "빠르게",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = bodyTextSize
                    ),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = maxW * 0.1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "확인",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = bodyTextSize
                    ),
                )
                Text(
                    "하실 수 있어요.",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = bodyTextSize
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.android_mockup2),
            contentDescription = null,
        )
    }
}


@Composable
@Preview(showBackground = true)
fun preview(){
//    OnboardingScreen(onFinish = {}, )
}
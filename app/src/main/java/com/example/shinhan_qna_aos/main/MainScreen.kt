package com.example.shinhan_qna_aos.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.info.api.InfoRepository
import com.example.shinhan_qna_aos.info.api.InfoViewModel
import com.example.shinhan_qna_aos.login.api.AuthRepository
import com.example.shinhan_qna_aos.login.api.LoginResult
import com.example.shinhan_qna_aos.login.api.LoginViewModel
import com.example.shinhan_qna_aos.main.api.AnswerRepository
import com.example.shinhan_qna_aos.main.api.PostRepository
import com.example.shinhan_qna_aos.main.api.TWPostRepository
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    postRepository: PostRepository,
    answerRepository: AnswerRepository,
    twPostRepository: TWPostRepository,
    infoRepository: InfoRepository,
    data: Data,
    navController: NavController,
    initialSelectedIndex: Int = 0
){
    val infoViewModel: InfoViewModel = viewModel(factory = SimpleViewModelFactory { InfoViewModel(infoRepository, data)})

    val navigationRoute by infoViewModel.navigationRoute.collectAsState()

    LaunchedEffect(navigationRoute) {
        navigationRoute?.let { route ->
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (route.isNotBlank() && currentRoute != route) {
                navController.navigate(route) {
                    // popUpTo를 사용하여 뒤로 가기 시 무한 루프에 빠지는 것을 방지합니다.
                    popUpTo("main") { inclusive = true }
                }
                Log.d("Navigation", "화면 이동: $route")
            }
        }
    }
    var selectedIndex by remember { mutableStateOf(initialSelectedIndex) }

    Box(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()){
        Column{
            MainTopbar(navController, data)
            Selectboard(
                postRepository = postRepository,
                answerRepository = answerRepository,
                twPostRepository = twPostRepository,
                data = data,
                navController = navController,
                selectedIndex = selectedIndex,
                onTabSelected = { idx -> selectedIndex = idx }
            )
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

// 서브 선택
@Composable
fun MainTopbar(navController: NavController,data: Data){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 18.dp, horizontal = 20.dp)
    ){
        Image(
            painter = painterResource(R.drawable.biglogo),
            modifier = Modifier.size(28.dp),
            contentDescription = null
        )
        TopIcon(navController, data)
    }
}

// 서브 선택
@Composable
fun TopIcon(navController: NavController, data: Data){
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)){
        Box(
            modifier = Modifier
                .border(1.dp, color = Color(0xffDFDFDF), RoundedCornerShape(10.dp))
                .padding(6.dp)
                .clickable { navController.navigate("writeBoard") }
        ) {
            Icon(
                painter = painterResource(lucide.plus),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
        }
        Box(
            modifier = Modifier
                .border(1.dp, color = Color(0xffDFDFDF), RoundedCornerShape(10.dp))
                .padding(6.dp)
                .clickable { navController.navigate("notices") }
        ) {
            Icon(
                painter = painterResource(R.drawable.shape),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
        }
        Box(
            modifier = Modifier
                .border(1.dp, color = Color(0xffDFDFDF), RoundedCornerShape(10.dp))
                .padding(6.dp)
                .clickable { navController.navigate("alarm") }
        ) {
            Icon(
                painter = painterResource(lucide.bell_ring),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
        }
        Box(
            modifier = Modifier
                .border(1.dp, color = Color(0xffDFDFDF), RoundedCornerShape(10.dp))
                .padding(6.dp)
                .clickable {
                    if(data.isAdmin){
                        navController.navigate("manager_myPage")
                    }else {
                        navController.navigate("mypage")
                    }
                }
        ){
            Icon(
                painter = painterResource(if(data.isAdmin)R.drawable.shield_user else lucide.user),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

//게시판 선택
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Selectboard(
    postRepository: PostRepository,
    answerRepository: AnswerRepository,
    twPostRepository: TWPostRepository,
    data: Data,
    navController: NavController,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {

    val tabData = listOf(
        Pair("말해봐요", R.drawable.trumpet),
        Pair("선정된 의견", R.drawable.star),
        Pair("답변 왔어요", R.drawable.check),
    )
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(26.dp )
        ) {
            tabData.forEachIndexed { idx, (title, imgRes) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = { onTabSelected(idx) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    TabWithAppleEmoji(
                        title = title,
                        imageRes = imgRes,
                        selected = selectedIndex == idx
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            val segmentCount = tabData.size
            val segmentWidth = screenWidthDp / segmentCount
            val blackBarRatio = 0.7f
            val blackBarWidth = segmentWidth * blackBarRatio

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xffA5A5A5), RoundedCornerShape(2.dp))
            )
            Box(
                modifier = Modifier
                    .offset(x = segmentWidth * selectedIndex + (segmentWidth - blackBarWidth) / 2)
                    .width(blackBarWidth)
                    .height(2.dp)
                    .background(Color.Black, RoundedCornerShape(2.dp))
            )
        }

        when (selectedIndex) {
            0 -> SaySomthingScreen(postRepository, data , navController)
            1 -> SelectedOpinionsScreen(twPostRepository, data, navController)
            2 -> AnsweredScreen(answerRepository, navController)
        }
    }
}

@Composable
fun TabWithAppleEmoji(
    title: String,
    imageRes: Int,
    selected: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = title,
            color = if (selected) Color.Black else Color(0xFFA5A5A5),
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
        )
    }
}


@Composable
@Preview(showBackground = true)
fun MainScreenPreview(){
//    MainScreen()
//    MainTopbar()
}
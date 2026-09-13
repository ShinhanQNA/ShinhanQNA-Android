package com.example.shinhan_qna_aos.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.DetailContent
import com.example.shinhan_qna_aos.InfoIconCount
import com.example.shinhan_qna_aos.SelectDataButton
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TitleContentLikeButton
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.main.api.TWPostRepository
import com.example.shinhan_qna_aos.main.api.TWPostViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedOpinionsScreen(twPostRepository: TWPostRepository, data: Data, navController: NavController) {
    val twPostViewModel: TWPostViewModel =
        viewModel(factory = SimpleViewModelFactory { TWPostViewModel(twPostRepository) })

    val opinions by twPostViewModel.opinions.collectAsState()

    // 주기적 로딩 작업을 관리하는 Job 상태
    var periodicJob by remember { mutableStateOf<Job?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // 앱이 포그라운드로 돌아오면 주기적 로딩 시작
                    periodicJob = coroutineScope.launch {
                        while (isActive) {
                            twPostViewModel.loadOpinions()
                            delay(60_000) // 1분마다 새로고침
                        }
                    }
                }
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    // 앱이 백그라운드로 가면 작업 취소
                    periodicJob?.cancel()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            periodicJob?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
    ) {
        items(opinions, key = { it.groupId }) { opinion ->
            SelectDataButton(
                year = LocalDate.now().year,
                month = opinion.selectedMonth,
                week = 3,
                isAdmin = data.isAdmin,
                responseState = opinion.responseStatus,
                onResponseStateChange = { newStatus ->
                    // 상태 변경 시 ViewModel에 API 호출
                    twPostViewModel.GroupStatusPost(opinion.groupId, newStatus)
                },
                onSelectDataClick = {
                    navController.navigate("threeWeekOpen/${opinion.groupId}")
                }
            )
            Divider()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedOpenScreen(
    groupId: Int,
    twPostRepository: TWPostRepository,
    navController: NavController
) {
    val twPostViewModel: TWPostViewModel = viewModel(
        factory = SimpleViewModelFactory { TWPostViewModel(twPostRepository) }
    )

    val groupDetailList by twPostViewModel.groupDetailList.collectAsState()
    val selectedSort by twPostViewModel.selectedSort.collectAsState()
    val selectedYear by twPostViewModel.selectedYear.collectAsState()
    val selectedMonth by twPostViewModel.selectedMonth.collectAsState()

    // 데이터를 불러오면서 연도, 월 값 같이 세팅
    LaunchedEffect(groupId, selectedSort) {
        twPostViewModel.loadGroupDetailPosts(groupId, selectedSort)
    }

    Box(
        modifier = Modifier.fillMaxSize().systemBarsPadding()
    ){
        Column {
            TopBar(
                title = "${selectedYear}년 ${selectedMonth}월 3주차",
                {
                    navController.navigate("main?selectedTab=1") {
                        popUpTo("threeWeekOpen/$groupId") { inclusive = true }
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                SortSelectionSection(
                    selectedSort = selectedSort,
                    onSortChange = { newSort -> twPostViewModel.changeSort(groupId, newSort) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(bottom = 50.dp)
            ) {
                items(groupDetailList) { opinion ->
                    TitleContentLikeButton(
                        title = opinion.title,
                        content = opinion.content,
                        likeCount = opinion.likes,
                        onClick = { navController.navigate("threeWeekDetail/${groupId}/${opinion.id}") }
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
fun SelectedDetailScreen(
    groupId: Int,
    twPostRepository: TWPostRepository,
    navController: NavController,
    id: Int
) {
    val twPostViewModel: TWPostViewModel =
        viewModel(factory = SimpleViewModelFactory { TWPostViewModel(twPostRepository) })

    val selectedSort by twPostViewModel.selectedSort.collectAsState()
    val groupDetailList by twPostViewModel.groupDetailList.collectAsState()
    Log.d("SelectedDetailScreen", "groupDetailList: $groupDetailList")
    Log.d("SelectedDetailScreen", "selectedSort: $selectedSort")

    // 화면 최초 진입 시 groupId, 기본 정렬 'date'로 상세 글 리스트 로드
    LaunchedEffect(groupId) {
        twPostViewModel.loadGroupDetailPosts(groupId, selectedSort)
    }

    // id에 해당하는 글을 리스트에서 찾기 (groupDetailList가 업데이트 될 때마다 재평가)
    val selectedPost = groupDetailList.firstOrNull { it.id == id }

    Box(
        modifier = Modifier.systemBarsPadding().fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = 50.dp)
        ) {
            TopBar(null) { navController.popBackStack() }
            DetailContent(title = selectedPost?.title ?: "", content = selectedPost?.content ?: "", imagePath = selectedPost?.imagePath)
            Box(modifier = Modifier.padding(horizontal = 20.dp)){ InfoIconCount(lucide.thumbs, "좋아요 표시", selectedPost?.likes ?: 0, Color.Black, 16) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // ModalBottomSheetState 및 ModalBottomSheet 사용을 위해 필요
@Composable
fun SortSelectionSection(
    selectedSort: String,
    onSortChange: (String) -> Unit,
) {
    // 모달 시트가 현재 보여지고 있는지 여부를 나타내는 상태입니다.
    var showModal by remember { mutableStateOf(false) }

    // 정렬 아이콘과 "정렬" 텍스트를 포함하는 행
    Row(
        modifier = Modifier
            .background(Color.White)
            .clickable { showModal = true }, // 클릭 시 모달을 보여주도록 상태 변경
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = lucide.filter), // 실제 리소스 ID로 변경해야 합니다.
            contentDescription = "정렬 아이콘",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "정렬",
            color = Color.Black,
            style = TextStyle(
                fontFamily = pretendard, // pretendard 폰트가 프로젝트에 정의되어 있어야 합니다.
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        )
    }

    // showModal 상태가 true일 때만 ModalBottomSheet를 렌더링합니다.
    if (showModal) {
        ModalBottomSheet(
            onDismissRequest = { showModal = false }, // 시트가 닫히도록 요청될 때 (바깥 클릭, 뒤로가기)
            containerColor = Color.White,
        ) {
            // 모달 시트의 내용
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .padding(20.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                val isDateSelected = selectedSort == "date"
                val isLikesSelected = selectedSort == "likes"
                Row(verticalAlignment = Alignment.CenterVertically){
                    Text(
                        "정렬",
                        color = Color(0xffA5A5A5),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Button(
                        onClick = {
                            onSortChange("date") // 정렬 기준 변경 콜백 호출
                            showModal = false   // 버튼 클릭 후 모달 닫기
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDateSelected) Color.Black else Color.White,
                            contentColor = if (isDateSelected) Color.White else Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        border = if (isDateSelected) null else BorderStroke(1.dp, Color(0xffDFDFDF))
                    ) {
                        Text(
                            text = "최신순",
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onSortChange("likes") // 정렬 기준 변경 콜백 호출
                            showModal = false   // 버튼 클릭 후 모달 닫기
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLikesSelected) Color.Black else Color.White,
                            contentColor = if (isLikesSelected) Color.White else Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        border = if (isLikesSelected) null else BorderStroke(1.dp, Color(0xffDFDFDF))
                    ) {
                        Text(
                            text = "공감순",
                            style = TextStyle(
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun SelectedOpinionsPreview(){
//    SelectedOpinionsScreen()
//    SelectedDetailScreen()
    SortSelectionSection(selectedSort = "date",onSortChange = {})
}
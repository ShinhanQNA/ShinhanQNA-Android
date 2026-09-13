package com.example.shinhan_qna_aos.main

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import com.example.shinhan_qna_aos.ManagerEditDeleteButton
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TitleContentButton
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.servepage.WriteInfo
import com.example.shinhan_qna_aos.servepage.WritingContentField
import com.example.shinhan_qna_aos.servepage.WritingTitleField
import com.example.shinhan_qna_aos.main.api.AnswerRepository
import com.example.shinhan_qna_aos.main.api.AnswerViewModel
import com.example.shinhan_qna_aos.main.api.UiAnswerRequest
import com.jihan.lucide_icons.lucide
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun AnsweredScreen(answerRepository: AnswerRepository, navController: NavController) {
    val answerViewModel: AnswerViewModel =
        viewModel(factory = SimpleViewModelFactory { AnswerViewModel(answerRepository) })

    val answerList by answerViewModel.answerList.collectAsState()

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
                            answerViewModel.loadAnswers()
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

        // Composable이 사라질 때 옵저버 제거 및 작업 취소
        onDispose {
            periodicJob?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 50.dp)) {
        items(answerList, key = {it.id}) { answer ->
            TitleContentButton(
                title = answer.title,
                content = answer.content,
                onClick = { navController.navigate("answerOpen/${answer.id}") }
            )
            Divider()
        }
    }
}

@Composable
fun AnsweredOpenScreen(
    answerRepository: AnswerRepository,
    navController: NavController,
    data: Data,
    id: Int
) {
    val answerViewModel: AnswerViewModel =
        viewModel(factory = SimpleViewModelFactory { AnswerViewModel(answerRepository) })

    val selectedAnswer by answerViewModel.selectedAnswer.collectAsState()
    val answerList by answerViewModel.answerList.collectAsState()

    val uiState = answerViewModel.answerstate

    // 최초에 리스트가 비어있거나 id가 바뀌면 답변 리스트 로드 후 id 검색
    LaunchedEffect(id, answerList) {
        if (answerList.isEmpty()) {
            answerViewModel.loadAnswers()
        }
        answerViewModel.selectAnswerById(id)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = 50.dp)
        ) {
            selectedAnswer?.let { answer ->

                TopBar(if (uiState.editMode) "게시글 수정" else null) {
                    if (uiState.editMode) {
                        navController.navigate("answerOpen/${answer.id}")
                    } else {
                        navController.navigate("main?selectedTab=2") {
                            popUpTo("answerOpen/${answer.id}") { inclusive = true }
                        }
                    }
                }

                if (uiState.editMode) {
                    AnswerEditPostContent(
                        uiState = uiState,
                        answerViewModel = answerViewModel,
                        id = id.toString(),
                        navController = navController
                    )
                } else {
                    LazyColumn {
                        item {
                            DetailContent(title = answer.title, content = answer.content)

                            if (data.isAdmin) {
                                ManagerEditDeleteButton(
                                    onDeleteClick = {
                                        Log.d("Compose", "삭제 버튼 클릭됨")
                                        answerViewModel.deleteAnswerPost(id)
                                        navController.popBackStack()
                                    },
                                    onEditClick = {
                                        answerViewModel.AnswerEditMode(
                                            selectedAnswer
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerEditPostContent(
    uiState: UiAnswerRequest,
    answerViewModel: AnswerViewModel,
    id: String,
    navController: NavController,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        Column {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WritingTitleField(
                        value = uiState.title,
                        onValueChange = answerViewModel::onTitleChange,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    WritingContentField(
                        value = uiState.content,
                        onValueChange = answerViewModel::onContentChange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                item {
                    WriteInfo()
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }

        //  수정 완료 버튼
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .clickable {
                    answerViewModel.updateAnswerPost(
                        id = id,
                        onSuccess = {
                            answerViewModel.selectAnswerById(id.toInt()) // 상세 조회 로드
                            answerViewModel.loadAnswers() // 전체 조회 로드
                            navController.navigate("answerOpen/$id")
                        },
                        onError = {
//                            Toast.makeText(context, "답변 게시글 수정 실패", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
        ) {
            Icon(
                painter = painterResource(lucide.cloud_upload),
                contentDescription = "작성하기",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Text("작성하기", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AnsweredPreview(){
//    AnsweredScreen()
}
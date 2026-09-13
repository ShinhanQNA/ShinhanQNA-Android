package com.example.shinhan_qna_aos.main

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TitleContentCountButton
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.main.api.PostRepository
import com.example.shinhan_qna_aos.main.api.PostViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun SaySomthingScreen(postRepository: PostRepository, data: Data, navController:NavController) {
    val postViewModel: PostViewModel =
        viewModel(factory = SimpleViewModelFactory { PostViewModel(postRepository) })

    val dataList = postViewModel.postList

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
                            postViewModel.loadPosts()
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


    LazyColumn {
        items(dataList, key = { it.postID }) { board ->
            TitleContentCountButton(
                title = board.title,
                content = board.content,
                likeCount = board.likeCount,
                isAdmin = data.isAdmin,
                flagsCount = board.flagsCount,
                banCount = board.banCount.toInt(),
                onClick = { navController.navigate("writeOpen/${board.postID}") }
            )
            Divider()
        }
    }
}

fun warningStatusToBanCount(warningStatus: String): String = when (warningStatus) {
    "없음" -> "0"
    "경고" -> "1"
    "차단" -> "2"
    else -> "0"
}

@Composable
@Preview(showBackground = true)
fun SayScreenPreview(){
//    SaySomthingScreen()
}
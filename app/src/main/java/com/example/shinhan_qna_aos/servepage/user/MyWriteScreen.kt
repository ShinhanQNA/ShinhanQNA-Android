package com.example.shinhan_qna_aos.etc.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TitleContentButton
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.main.api.PostRepository
import com.example.shinhan_qna_aos.main.api.PostViewModel

@Composable
fun MyWriteScreen(postRepository: PostRepository, navController: NavController){
    val postViewModel:PostViewModel = viewModel(factory = SimpleViewModelFactory() { PostViewModel(postRepository) })
    val myPostList = postViewModel.myPostList

    LaunchedEffect (Unit){
        postViewModel.loadMyPosts()
    }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding().background(Color.White)) {
        TopBar("내가 작성한 게시글",{navController.popBackStack()})
        Box(){
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp)) {
                items(myPostList) { data ->
                    TitleContentButton(
                        title = data.title,
                        content = data.content,
                        onClick = { navController.navigate("writeOpen/${data.postID}") }
                    )
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

@Preview(showBackground = true)
@Composable
fun MyWritePreview(){
//    MyWriteScreen()
}
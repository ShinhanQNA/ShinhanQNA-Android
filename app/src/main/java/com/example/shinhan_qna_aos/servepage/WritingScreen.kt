package com.example.shinhan_qna_aos.servepage

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.TopBar
import com.example.shinhan_qna_aos.servepage.api.WriteRepository
import com.example.shinhan_qna_aos.servepage.api.WritingViewModel
import com.example.shinhan_qna_aos.main.api.AnswerRepository
import com.example.shinhan_qna_aos.main.api.AnswerViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide

@Composable
fun WritingScreen(writeRepository: WriteRepository, answerRepository: AnswerRepository, navController: NavController, data: Data) {
    val context = LocalContext.current
    val writingViewModel: WritingViewModel =
        viewModel(factory = SimpleViewModelFactory { WritingViewModel(writeRepository) })
    val answerViewModel: AnswerViewModel =
        viewModel(factory = SimpleViewModelFactory { AnswerViewModel(answerRepository) })

    val state = writingViewModel.state
    val answerstae = answerViewModel.answerstate
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                writingViewModel.onImageChange(context, it)
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White)
            .imePadding() // 키보드에 반응
    ) {
        Column {
            TopBar("게시글 작성", { navController.navigate("main?selectedTab=0") {popUpTo("writeBoard"){inclusive=true} }})

            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WritingTitleField(
                        value = if (data.isAdmin) answerstae.title else state.title,
                        onValueChange = if(data.isAdmin) answerViewModel::onTitleChange else writingViewModel::onTitleChange,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    WritingContentField(
                        value = if (data.isAdmin) answerstae.content else state.content,
                        onValueChange = if(data.isAdmin) answerViewModel::onContentChange else writingViewModel::onContentChange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                item {
                    if (!data.isAdmin) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .background(Color.Black, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clickable { launcher.launch("image/*") },
                            ) {
                                Icon(
                                    painter = painterResource(lucide.images),
                                    contentDescription = "사진 첨부",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White
                                )
                                Text("사진 첨부", color = Color.White, fontSize = 14.sp)
                            }
                            Text(
                                state.imageUri?.lastPathSegment ?: "",
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    WriteInfo()
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
        // FAB처럼 동작하는 커스텀 버튼
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd) // 화면 오른쪽 하단 고정
                .padding(20.dp)             // FAB 기본 여백 느낌
                .background(Color.Black, RoundedCornerShape(12.dp))
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .clickable {
                    if (data.isAdmin) {
                        answerViewModel.writeAnswer(
                            onSusscess = {
                                navController.navigate("main?selectedTab=2") {
                                    popUpTo("writeBoard") { inclusive = true }
                                }
                            }
                        )
                    } else {
                        writingViewModel.uploadPost(
                            onSuccess = {
                                navController.navigate("main?selectedTab=0") {
                                    popUpTo("writeBoard") { inclusive = true }
                                }
                            },
                            onError = {
                                Toast.makeText(context, "게시글 작성이 실패하였습니다", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
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

// 제목 입력 필드
@Composable
fun WritingTitleField(
    value: String?,
    onValueChange: (String) -> Unit,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 36.dp)
            .border(1.dp, Color(0xFFdfdfdf), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (value.isNullOrEmpty()) {
            Text(
                text = "제목을 입력해주세요.",
                color = Color(0xffDFDFDF),
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = pretendard,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        BasicTextField(
            value = value ?: "",
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = pretendard,
                lineHeight = 32.sp
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// 본문 입력 필드
@Composable
fun WritingContentField(
    value: String?,
    onValueChange: (String) -> Unit,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 320.dp)  // 최소 높이 지정
            .border(1.dp, Color(0xFFdfdfdf), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (value.isNullOrEmpty()) {
            Text(
                text = "정확한 전달을 위해 교수님 성함 혹은 과목명을 정확하게 기재해주세요.",
                color = Color(0xffDFDFDF),
                fontSize = fontSize,
                fontFamily = pretendard,
                fontWeight = fontWeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
                lineHeight = 20.sp
            )
        }
        BasicTextField(
            value = value ?: "",
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = pretendard,
                lineHeight = 28.sp
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WriteInfo(){
    Text("주의사항 및 안내사항\n" +
            "건전하고 유익한 커뮤니티 환경을 위해 다음 사항을 준수해주세요.\n" +
            "\n" +
            "특정 개인이나 단체에 대한 비방, 욕설, 혐오 발언 등 다른 사용자에게 불쾌감을 주는 게시물은 금지됩니다.\n" +
            "\n" +
            "본인 및 타인의 개인정보(이름, 연락처, 학번, 주소 등)를 무단으로 게시할 경우, 법적인 문제가 발생할 수 있습니다.\n" +
            "\n" +
            "확인되지 않은 허위사실을 유포하거나 타인의 명예를 훼손하는 내용은 작성할 수 없습니다.\n" +
            "\n" +
            "상업적 목적의 광고, 홍보성 게시물 및 도배성 게시물은 제재 대상이 될 수 있습니다.\n" +
            "\n" +
            "위의 사항에 위배되는 게시글은 사전 통보 없이 삭제될 수 있으며, 서비스 이용이 제한될 수 있습니다.\n" +
            "\n" +
            "게시글에 대한 법적 책임은 전적으로 작성자 본인에게 있습니다.",
        color = Color(0xffA5A5A5),
        style = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
    )
}

@Preview(showBackground = true)
@Composable
fun WritinScreenPreview(){
//    val writingViewModel = WritingViewModel()
    val navController : NavHostController = rememberNavController()
//    WritingScreen(navController = navController)
}
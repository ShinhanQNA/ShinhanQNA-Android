package com.example.shinhan_qna_aos.info

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.SimpleViewModelFactory
import com.example.shinhan_qna_aos.Data
import com.example.shinhan_qna_aos.LabeledField
import com.example.shinhan_qna_aos.PlainInputField
import com.example.shinhan_qna_aos.info.api.InfoRepository
import com.example.shinhan_qna_aos.info.api.InfoViewModel
import com.example.shinhan_qna_aos.ui.theme.pretendard
import com.jihan.lucide_icons.lucide
import kotlinx.coroutines.delay

@Composable
fun InformationScreen(infoRepository: InfoRepository, data: Data, navController: NavController) {
    val context = LocalContext.current
    val infoViewModel: InfoViewModel = viewModel(factory = SimpleViewModelFactory { InfoViewModel(infoRepository, data) })

    val uiState by infoViewModel.uiState.collectAsState()
    val navigationRoute by infoViewModel.navigationRoute.collectAsState()
    // 드랍시트 관리
    var expandedGrade by remember { mutableStateOf(false) }
    var expandedMajor by remember { mutableStateOf(false) }

    // 가입 요청 모든 필드 입력시에만 누를 수 있도록
    val isFormValid = remember(uiState) { uiState.name.isNotBlank() && uiState.students != 0 && uiState.year != 0 && uiState.department.isNotBlank() && uiState.imageUri != Uri.EMPTY }

    // navigationRoute가 변경될 때만 네비게이션 실행 (null 체크 포함)
    LaunchedEffect(navigationRoute) {
        navigationRoute?.let { route ->
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (route.isNotBlank() && currentRoute != route) {
                navController.navigate(route) {
                    popUpTo("info") { inclusive = true }
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White)
    ) {
        val maxWidthPx = maxWidth
        val maxHeightPx = maxHeight
        val aspectRatio = maxWidthPx / maxHeightPx

        val (horizontalPadding, contentWidthFraction, logoSize) = when {
            maxWidthPx <= 600.dp -> Triple(40.dp, 0.88f, if (aspectRatio < 1f) 80.dp else 96.dp)
            maxWidthPx <= 840.dp -> Triple(32.dp, 0.7f, if (aspectRatio < 1f) 112.dp else 100.dp)
            else -> Triple(64.dp, 0.5f, if (aspectRatio < 1.2f) 128.dp else 110.dp)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.biglogo),
                contentDescription = "Logo",
                modifier = Modifier.size(logoSize)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(contentWidthFraction)
            ) {
                NameField(
                    value = uiState.name,
                    onValueChange = infoViewModel::onNameChange,
                    fontSize = 14.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StudentIdField(
                        value = uiState.students,
                        onValueChange = infoViewModel::onStudentIdChange,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(0.55f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    GradeDropdown(
                        selected = uiState.year,
                        onSelectedChange = infoViewModel::onGradeChange,
                        options = listOf("1학년", "2학년", "3학년", "4학년"),
                        expanded = expandedGrade,
                        onExpandedChange = { expandedGrade = it },
                        fontSize = 14.sp,
                        modifier = Modifier.weight(0.45f)
                    )
                }
                MajorDropdown(
                    selected = uiState.department,
                    onSelectedChange = infoViewModel::onMajorChange,
                    options = listOf("소프트웨어융합"),
                    expanded = expandedMajor,
                    onExpandedChange = {expandedMajor=it},
                    fontSize = 14.sp,
                )
                ImageInsert(viewModel = infoViewModel, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(36.dp))
            Request(
                fontSize = 14.sp,
                onClick = {
                    infoViewModel.submitStudentInfo(context)
                },
                enabled = isFormValid)
        }
    }
}

// 드롭다운 필드 (공통)
@Composable
fun DropDownField(
    label: String,
    selected: String?,
    onSelectedChange: (String) -> Unit,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    LabeledField(label, fontSize, modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFdfdfdf), RoundedCornerShape(10.dp))
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?:"",
                    fontSize = fontSize,
                    fontFamily = pretendard,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(lucide.chevron_down),
                    modifier = Modifier.size(18.dp),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier
                    .widthIn(min = 180.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = fontSize, fontFamily = pretendard) },
                        onClick = {
                            onSelectedChange(option)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}

// 개별 필드/드롭다운 컴포저블(재사용)
@Composable
fun NameField(value: String, onValueChange: (String) -> Unit, fontSize: TextUnit) =
    LabeledField("이름", fontSize) {
        PlainInputField(value, onValueChange, fontSize)
    }

@Composable
fun StudentIdField(
    value: Int,
    onValueChange: (String) -> Unit,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) = LabeledField("학번", fontSize, modifier) {
    val displayValue = if (value == 0) "" else value.toString()
    PlainInputField(
        value = displayValue,
        onValueChange = { newValue ->
            if (newValue.length <= 8 && newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        fontSize = fontSize,
        keyboardType = KeyboardType.Number,
    )
}

@Composable
fun GradeDropdown(
    selected: Int,
    onSelectedChange: (String) -> Unit,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    val selectedText = if (selected == 0) "" else "${selected}학년"
    DropDownField(
        label = "학년",
        selected = selectedText,
        onSelectedChange = onSelectedChange,
        options = options,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        fontSize = fontSize,
        modifier = modifier
    )
}

@Composable
fun MajorDropdown(
    selected: String,
    onSelectedChange: (String) -> Unit,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) = DropDownField(
    label = "학과",
    selected = selected,
    onSelectedChange = onSelectedChange,
    options = options,
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    fontSize = fontSize,
    modifier = modifier
)
// 이미지 첨부 영역
@Composable
fun ImageInsert(viewModel: InfoViewModel, fontSize: TextUnit) {

    val uiState by viewModel.uiState.collectAsState()
    val imageUri = uiState.imageUri

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.onImageChange(it) }
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "재학 확인서 첨부(학생증, 재학증명서)",
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier.defaultMinSize(minHeight = 36.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = "사진 첨부",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = fontSize,
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = imageUri.lastPathSegment ?: "첨부된 사진이 없습니다.",
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// 가입 요청 버튼
@Composable
fun Request(
    fontSize: TextUnit,
    onClick: ()-> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .background(
                color = if (enabled) Color.Black else Color(0xffC2C2C2),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .clickable(enabled = enabled) { onClick() }
    ) {
        Text(
            text = "가입요청",
            color = Color.White,
            style = TextStyle(
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize,
            )
        )
    }
}

//
//@Composable
//@Preview(showBackground = true)
//fun Informationpreview(){
////    val viewModel : InfoViewModel = viewModel()
////    InformationScreen(viewModel)
//}
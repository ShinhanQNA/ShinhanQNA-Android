package com.example.shinhan_qna_aos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shinhan_qna_aos.API.APIInterface
import com.example.shinhan_qna_aos.etc.user.MyWriteScreen
import com.example.shinhan_qna_aos.servepage.WriteOpenScreen
import com.example.shinhan_qna_aos.servepage.WritingScreen
import com.example.shinhan_qna_aos.servepage.api.WriteRepository
import com.example.shinhan_qna_aos.servepage.user.AppealScreen1
import com.example.shinhan_qna_aos.servepage.user.AppealScreen2
import com.example.shinhan_qna_aos.servepage.user.AppealScreen3
import com.example.shinhan_qna_aos.info.api.InfoRepository
import com.example.shinhan_qna_aos.info.InformationScreen
import com.example.shinhan_qna_aos.info.WaitScreen
import com.example.shinhan_qna_aos.info.api.InfoViewModel
import com.example.shinhan_qna_aos.login.api.AuthRepository
import com.example.shinhan_qna_aos.login.api.AuthSession
import com.example.shinhan_qna_aos.login.api.AuthViewModel
import com.example.shinhan_qna_aos.login.LoginScreen
import com.example.shinhan_qna_aos.login.ManagerLoginScreen
import com.example.shinhan_qna_aos.login.api.routeForAuthSession
import com.example.shinhan_qna_aos.main.AnsweredOpenScreen
import com.example.shinhan_qna_aos.main.AnsweredScreen
import com.example.shinhan_qna_aos.main.MainScreen
import com.example.shinhan_qna_aos.main.SelectedDetailScreen
import com.example.shinhan_qna_aos.main.SelectedOpenScreen
import com.example.shinhan_qna_aos.main.api.AnswerRepository
import com.example.shinhan_qna_aos.main.api.PostRepository
import com.example.shinhan_qna_aos.main.api.TWPostRepository
import com.example.shinhan_qna_aos.onboarding.OnboardingScreen
import com.example.shinhan_qna_aos.servepage.AlarmScreen
import com.example.shinhan_qna_aos.servepage.AlarmViewModel
import com.example.shinhan_qna_aos.servepage.user.MypageScreen
import com.example.shinhan_qna_aos.servepage.NotificationOpenScreen
import com.example.shinhan_qna_aos.servepage.NotificationScreen
import com.example.shinhan_qna_aos.servepage.api.AppealRepository
import com.example.shinhan_qna_aos.servepage.api.NotificationRepository
import com.example.shinhan_qna_aos.servepage.manager.AccessionDetailScreen
import com.example.shinhan_qna_aos.servepage.manager.AccessionScreen
import com.example.shinhan_qna_aos.servepage.manager.BanClearDetailScreen
import com.example.shinhan_qna_aos.servepage.manager.BanClearPostScreen
import com.example.shinhan_qna_aos.servepage.manager.BanClearScreen
import com.example.shinhan_qna_aos.servepage.manager.DeclarationOpenScreen
import com.example.shinhan_qna_aos.servepage.manager.DeclarationScreen
import com.example.shinhan_qna_aos.servepage.manager.ManagerScreen
import com.example.shinhan_qna_aos.servepage.manager.NotificationWriteScreen
import com.example.shinhan_qna_aos.servepage.manager.api.AccessionRepository
import com.example.shinhan_qna_aos.servepage.manager.api.BanClearRepository
import com.example.shinhan_qna_aos.servepage.manager.api.DeclarationRepository
import com.example.shinhan_qna_aos.servepage.user.RefuseScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    apiInterface: APIInterface,
    notificationLaunch: NotificationLaunch? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // Data & Repository
    val data = remember { Data(context) }
    val authRepository = remember { AuthRepository(apiInterface, data) }
    val writeRepository = remember { WriteRepository(apiInterface, data) }
    val postRepository = remember { PostRepository(apiInterface, data) }
    val infoRepository = remember { InfoRepository(apiInterface, data) }
    val answerRepository = remember { AnswerRepository(apiInterface,data) }
    val twPostRepository= remember { TWPostRepository(apiInterface, data) }
    val notificationRepository = remember { NotificationRepository(apiInterface, data) }
    val declarationRepository = remember { DeclarationRepository(data, apiInterface) }
    val appealRepository = remember { AppealRepository(apiInterface, data) }
    val accessionRepository = remember{ AccessionRepository(data, apiInterface) }
    val banClearRepository = remember { BanClearRepository(apiInterface, data) }

    val authViewModel: AuthViewModel =
        viewModel(factory = SimpleViewModelFactory { AuthViewModel(authRepository, infoRepository, data) })
    val infoViewModel: InfoViewModel =
        viewModel(factory = SimpleViewModelFactory { InfoViewModel(infoRepository) })
    val alarmViewModel: AlarmViewModel =
        viewModel(factory = SimpleViewModelFactory { AlarmViewModel(context) })
    val pushTokenRegistrar = remember { PushTokenRegistrar(context.applicationContext, apiInterface, data) }

    val authState by authViewModel.state.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var handledNotificationEvent by remember { mutableStateOf<Long?>(null) }
    val authRoute = if (data.onboarding) "onboarding" else routeForAuthSession(authState.session)

    LaunchedEffect(authState.session) {
        if (authState.isAuthenticated) pushTokenRegistrar.syncCurrentToken()
    }

    LaunchedEffect(notificationLaunch, authState.session, currentBackStackEntry) {
        val launch = notificationLaunch ?: return@LaunchedEffect
        if (handledNotificationEvent == launch.eventId || !authState.isAuthenticated) return@LaunchedEffect
        if (!authState.canOpenNotifications) {
            handledNotificationEvent = launch.eventId
            return@LaunchedEffect
        }
        val currentRoute = currentBackStackEntry?.destination?.route ?: return@LaunchedEffect
        if (data.onboarding || currentRoute == "login" || currentRoute == "onboarding") return@LaunchedEffect
        navController.navigate(launch.route ?: "alarm") { launchSingleTop = true }
        alarmViewModel.markRead(launch.key)
        handledNotificationEvent = launch.eventId
    }

    // 앱 최초 진입 시 빠르게 보여줄 초기 화면 결정용 상태
    var initialRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authRoute) {
        authRoute?.let { route ->
            if (initialRoute == null) {
                initialRoute = route
                debugLog("AppNavigation", "초기 화면 경로를 설정했습니다.")
            } else if (navController.currentBackStackEntry?.destination?.route != route) {
                navController.navigate(route) {
                    popUpTo(0) { inclusive = true }
                }
                debugLog("AppNavigation", "화면 경로가 변경되었습니다.")
            }
        }
    }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                authViewModel.onAppResumed()
                debugLog("Lifecycle", "앱이 다시 활성화되었습니다.")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 초기 라우트가 null 이면 NavHost 렌더링 안 함 (startDestination에 빈값 전달 방지)
    if (initialRoute.isNullOrBlank()) return

    NavHost(
        navController = navController,
        startDestination = initialRoute!!
    ) {
        composable("onboarding") { OnboardingScreen(navController, data) }  // 온보딩
        composable("login") {
            LoginScreen(
                onKakaoLogin = authViewModel::loginWithKakao,
                onGoogleLogin = authViewModel::loginWithGoogle,
                onManagerLogin = { navController.navigate("manager_login") }
            )
        }
        composable("manager_login") {
            ManagerLoginScreen(
                state = authViewModel.managerLoginData,
                onIdChange = authViewModel::onAdminIdChange,
                onPasswordChange = authViewModel::onAdminPasswordChange,
                onLogin = authViewModel::loginAdmin
            )
        }
        composable("info") {
            InformationScreen(
                infoViewModel = infoViewModel,
                reapplying = authState.session is AuthSession.Reapplying,
                onSubmitted = authViewModel::onStudentInfoSubmitted
            )
        }
        composable("wait") { WaitScreen(data) }
        composable("refuse") { RefuseScreen(data, authViewModel::beginReapplication) }
        composable( // 메인 화면 선택 사항이 많아서 selectedTab으로 원하는 화면으로 조정 가능
            "main?selectedTab={selectedTab}",
            arguments = listOf(navArgument("selectedTab") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val selectedTab = backStackEntry.arguments?.getInt("selectedTab") ?: 0
            MainScreen(
                postRepository = postRepository,
                answerRepository = answerRepository,
                twPostRepository = twPostRepository,
                data = data,
                navController = navController,
                initialSelectedIndex = selectedTab
            )
        }
        composable( // 게시글 상세 화면
            "writeOpen/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            WriteOpenScreen(navController, postRepository, writeRepository, data, postId)
        }

        composable("writeBoard") { WritingScreen(writeRepository, answerRepository, navController, data) }
        composable("answer") { AnsweredScreen(answerRepository, navController) } // 답변 화면

        composable( // 답변 상세 화면
            "answerOpen/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            AnsweredOpenScreen(answerRepository, navController, data, id)
        }

        composable( // 3주차 게시판 리스트로 있음
            "threeWeekOpen/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
            SelectedOpenScreen(groupId, twPostRepository, navController)
        }

        composable( // 3주차 게시판 상세화면
            "threeWeekDetail/{groupId}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType },navArgument("groupId") { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            SelectedDetailScreen(groupId , twPostRepository, navController, id)
        }

        composable("myPage") { MypageScreen(navController, authViewModel::logout, authViewModel::cancelMember) }
        composable("manager_myPage"){ ManagerScreen(navController, authViewModel::logout) }

        composable("my_page_write") { MyWriteScreen(postRepository, navController) } // 내가 작성한 게시글

        composable("notices") { NotificationScreen(data, notificationRepository, navController) }
        composable( // 공지 상세 화면
            "notices/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: -1
            NotificationOpenScreen(id, data, notificationRepository, navController)
        }
        composable("notices_write"){ NotificationWriteScreen(notificationRepository, navController) } // 관리자 공지 작성 화면

        composable("alarm") { AlarmScreen(navController, alarmViewModel) }

        composable("appeal1"){ AppealScreen1(appealRepository, data, navController) }
        composable("appeal2"){ AppealScreen2(appealRepository, navController, authViewModel::onAppealSubmitted) }
        composable("appeal3"){ AppealScreen3(data) }

        composable("declaration") { DeclarationScreen(declarationRepository, postRepository, data, navController) }
        composable( // 신고된 게시글 상세
            "declaration/{postId}/{reportId}",
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType },
                navArgument("reportId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val reportId = backStackEntry.arguments?.getInt("reportId") ?: -1
            DeclarationOpenScreen(postId, reportId, navController, postRepository, declarationRepository)
        }

        composable("accession") { AccessionScreen(accessionRepository, navController) } // 가입 신청자
        composable("accessionDetail/{email}", arguments = listOf(navArgument("email") { type = NavType.StringType }) // 가입 신청 상세 글
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            AccessionDetailScreen(accessionRepository, navController, email)
        }

        composable("banclear") { BanClearScreen(banClearRepository, navController) } // 가입 신청자
        composable("banclearDetail/{email}", arguments = listOf(navArgument("email") { type = NavType.StringType }) // 가입 신청 상세 글
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            BanClearDetailScreen(banClearRepository, navController, email)
        }
        composable("banclearPost/{email}/{postId}", arguments = listOf(navArgument("email") { type = NavType.StringType },navArgument("postId") { type = NavType.StringType }) // 가입 신청 상세 글
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            BanClearPostScreen(banClearRepository, navController, email, postId.toInt(), data)
        }
    }
}

// 공통 ViewModelFactory 구현
class SimpleViewModelFactory<T: ViewModel>(
    private val creator: () -> T
): ViewModelProvider.Factory {
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
}

internal fun routeForNotification(type: String?, id: String?): String? = when (type) {
    "notice" -> id?.toIntOrNull()?.takeIf { it > 0 }?.let { "notices/$it" }
    "answer" -> id?.toIntOrNull()?.takeIf { it > 0 }?.let { "answerOpen/$it" }
    "post" -> id?.toIntOrNull()?.takeIf { it > 0 }?.let { "writeOpen/$it" }
    else -> null
}

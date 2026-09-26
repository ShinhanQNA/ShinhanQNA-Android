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
import androidx.compose.runtime.saveable.rememberSaveable
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
    var handledNotificationKey by rememberSaveable { mutableStateOf<String?>(null) }
    val authRoute = if (data.onboarding) AppRoute.ONBOARDING else AppRoute.forAuthSession(authState.session)

    LaunchedEffect(authState.session) {
        if (authState.isAuthenticated) pushTokenRegistrar.syncCurrentToken()
    }

    LaunchedEffect(notificationLaunch, authState.session, currentBackStackEntry) {
        val currentEntry = currentBackStackEntry
        val currentRoute = currentEntry?.destination?.route
        val currentTargetId = when (currentRoute) {
            AppRoute.WRITE_OPEN_PATTERN -> currentEntry?.arguments?.getString(AppRoute.ARG_POST_ID)
            AppRoute.ANSWER_OPEN_PATTERN, AppRoute.NOTICE_OPEN_PATTERN ->
                currentEntry?.arguments?.getInt(AppRoute.ARG_ID)?.takeIf { it > 0 }?.toString()
            else -> null
        }

        val decision = notificationNavigationDecision(
            launch = notificationLaunch,
            handledKey = handledNotificationKey,
            isAuthenticated = authState.isAuthenticated,
            canOpenNotifications = authState.canOpenNotifications,
            onboarding = data.onboarding,
            currentRoute = currentRoute,
            currentTargetId = currentTargetId
        )
        if (!decision.consume) return@LaunchedEffect

        decision.route?.let { route ->
            navController.navigate(route) { launchSingleTop = true }
        }
        if (decision.markRead) notificationLaunch?.let { alarmViewModel.markRead(it.key) }
        handledNotificationKey = notificationLaunch?.key
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
        composable(AppRoute.ONBOARDING) { OnboardingScreen(navController, data) }  // 온보딩
        composable(AppRoute.LOGIN) {
            LoginScreen(
                onKakaoLogin = authViewModel::loginWithKakao,
                onGoogleLogin = authViewModel::loginWithGoogle,
                onManagerLogin = { navController.navigate(AppRoute.MANAGER_LOGIN) }
            )
        }
        composable(AppRoute.MANAGER_LOGIN) {
            ManagerLoginScreen(
                state = authViewModel.managerLoginData,
                onIdChange = authViewModel::onAdminIdChange,
                onPasswordChange = authViewModel::onAdminPasswordChange,
                onLogin = authViewModel::loginAdmin
            )
        }
        composable(AppRoute.INFO) {
            InformationScreen(
                infoViewModel = infoViewModel,
                reapplying = authState.session is AuthSession.Reapplying,
                onSubmitted = authViewModel::onStudentInfoSubmitted
            )
        }
        composable(AppRoute.WAIT) { WaitScreen(data) }
        composable(AppRoute.REFUSE) { RefuseScreen(data, authViewModel::beginReapplication) }
        composable( // 메인 화면 선택 사항이 많아서 selectedTab으로 원하는 화면으로 조정 가능
            AppRoute.MAIN_PATTERN,
            arguments = listOf(navArgument(AppRoute.ARG_SELECTED_TAB) {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val selectedTab = backStackEntry.arguments?.getInt(AppRoute.ARG_SELECTED_TAB) ?: 0
            MainScreen(
                postRepository = postRepository,
                answerRepository = answerRepository,
                twPostRepository = twPostRepository,
                isAdmin = authState.isAdmin,
                navController = navController,
                initialSelectedIndex = selectedTab
            )
        }
        composable( // 게시글 상세 화면
            AppRoute.WRITE_OPEN_PATTERN,
            arguments = listOf(navArgument(AppRoute.ARG_POST_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString(AppRoute.ARG_POST_ID) ?: ""
            WriteOpenScreen(navController, postRepository, writeRepository, data, authState.isAdmin, postId)
        }

        composable(AppRoute.WRITE_BOARD) { WritingScreen(writeRepository, answerRepository, navController, authState.isAdmin) }
        composable(AppRoute.ANSWER) { AnsweredScreen(answerRepository, navController) } // 답변 화면

        composable( // 답변 상세 화면
            AppRoute.ANSWER_OPEN_PATTERN,
            arguments = listOf(navArgument(AppRoute.ARG_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(AppRoute.ARG_ID) ?: -1
            AnsweredOpenScreen(answerRepository, navController, authState.isAdmin, id)
        }

        composable( // 3주차 게시판 리스트로 있음
            AppRoute.THREE_WEEK_OPEN_PATTERN,
            arguments = listOf(navArgument(AppRoute.ARG_GROUP_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt(AppRoute.ARG_GROUP_ID) ?: -1
            SelectedOpenScreen(groupId, twPostRepository, navController)
        }

        composable( // 3주차 게시판 상세화면
            AppRoute.THREE_WEEK_DETAIL_PATTERN,
            arguments = listOf(
                navArgument(AppRoute.ARG_ID) { type = NavType.IntType },
                navArgument(AppRoute.ARG_GROUP_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt(AppRoute.ARG_GROUP_ID) ?: -1
            val id = backStackEntry.arguments?.getInt(AppRoute.ARG_ID) ?: -1
            SelectedDetailScreen(groupId , twPostRepository, navController, id)
        }

        composable(AppRoute.MY_PAGE) { MypageScreen(navController, authViewModel::logout, authViewModel::cancelMember) }
        composable(AppRoute.MANAGER_MY_PAGE){ ManagerScreen(navController, authViewModel::logout) }

        composable(AppRoute.MY_PAGE_WRITE) { MyWriteScreen(postRepository, navController) } // 내가 작성한 게시글

        composable(AppRoute.NOTICES) { NotificationScreen(authState.isAdmin, notificationRepository, navController) }
        composable( // 공지 상세 화면
            AppRoute.NOTICE_OPEN_PATTERN,
            arguments = listOf(navArgument(AppRoute.ARG_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(AppRoute.ARG_ID) ?: -1
            NotificationOpenScreen(id, authState.isAdmin, notificationRepository, navController)
        }
        composable(AppRoute.NOTICES_WRITE){ NotificationWriteScreen(notificationRepository, navController) } // 관리자 공지 작성 화면

        composable(AppRoute.ALARM) { AlarmScreen(navController, alarmViewModel) }

        composable(AppRoute.APPEAL_1){ AppealScreen1(appealRepository, data, navController) }
        composable(AppRoute.APPEAL_2){ AppealScreen2(appealRepository, navController, authViewModel::onAppealSubmitted) }
        composable(AppRoute.APPEAL_3){ AppealScreen3(data) }

        composable(AppRoute.DECLARATION) { DeclarationScreen(declarationRepository, postRepository, authState.isAdmin, navController) }
        composable( // 신고된 게시글 상세
            AppRoute.DECLARATION_DETAIL_PATTERN,
            arguments = listOf(
                navArgument(AppRoute.ARG_POST_ID) { type = NavType.StringType },
                navArgument(AppRoute.ARG_REPORT_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString(AppRoute.ARG_POST_ID) ?: ""
            val reportId = backStackEntry.arguments?.getInt(AppRoute.ARG_REPORT_ID) ?: -1
            DeclarationOpenScreen(postId, reportId, navController, postRepository, declarationRepository)
        }

        composable(AppRoute.ACCESSION) { AccessionScreen(accessionRepository, navController) } // 가입 신청자
        composable(AppRoute.ACCESSION_DETAIL_PATTERN, arguments = listOf(navArgument(AppRoute.ARG_EMAIL) { type = NavType.StringType }) // 가입 신청 상세 글
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(AppRoute.ARG_EMAIL) ?: ""
            AccessionDetailScreen(accessionRepository, navController, email)
        }

        composable(AppRoute.BAN_CLEAR) { BanClearScreen(banClearRepository, navController) } // 가입 신청자
        composable(AppRoute.BAN_CLEAR_DETAIL_PATTERN, arguments = listOf(navArgument(AppRoute.ARG_EMAIL) { type = NavType.StringType }) // 가입 신청 상세 글
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(AppRoute.ARG_EMAIL) ?: ""
            BanClearDetailScreen(banClearRepository, navController, email)
        }
        composable(
            AppRoute.BAN_CLEAR_POST_PATTERN,
            arguments = listOf(
                navArgument(AppRoute.ARG_EMAIL) { type = NavType.StringType },
                navArgument(AppRoute.ARG_POST_ID) { type = NavType.StringType }
            ) // 가입 신청 상세 글
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(AppRoute.ARG_EMAIL) ?: ""
            val postId = backStackEntry.arguments?.getString(AppRoute.ARG_POST_ID) ?: ""
            BanClearPostScreen(banClearRepository, navController, email, postId.toInt(), authState.isAdmin)
        }
    }
}

// 공통 ViewModelFactory 구현
class SimpleViewModelFactory<T: ViewModel>(
    private val creator: () -> T
): ViewModelProvider.Factory {
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
}

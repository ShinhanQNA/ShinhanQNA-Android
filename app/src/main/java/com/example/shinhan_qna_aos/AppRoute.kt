package com.example.shinhan_qna_aos

import com.example.shinhan_qna_aos.login.api.AuthSession

internal object AppRoute {
    const val ARG_SELECTED_TAB = "selectedTab"
    const val ARG_POST_ID = "postId"
    const val ARG_ID = "id"
    const val ARG_GROUP_ID = "groupId"
    const val ARG_EMAIL = "email"
    const val ARG_REPORT_ID = "reportId"

    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val MANAGER_LOGIN = "manager_login"
    const val INFO = "info"
    const val WAIT = "wait"
    const val REFUSE = "refuse"
    const val MAIN = "main"
    const val MAIN_PATTERN = "$MAIN?$ARG_SELECTED_TAB={$ARG_SELECTED_TAB}"
    const val WRITE_OPEN_PATTERN = "writeOpen/{$ARG_POST_ID}"
    const val WRITE_BOARD = "writeBoard"
    const val ANSWER = "answer"
    const val ANSWER_OPEN_PATTERN = "answerOpen/{$ARG_ID}"
    const val THREE_WEEK_OPEN_PATTERN = "threeWeekOpen/{$ARG_GROUP_ID}"
    const val THREE_WEEK_DETAIL_PATTERN = "threeWeekDetail/{$ARG_GROUP_ID}/{$ARG_ID}"
    const val MY_PAGE = "myPage"
    const val MANAGER_MY_PAGE = "manager_myPage"
    const val MY_PAGE_WRITE = "my_page_write"
    const val NOTICES = "notices"
    const val NOTICE_OPEN_PATTERN = "notices/{$ARG_ID}"
    const val NOTICES_WRITE = "notices_write"
    const val ALARM = "alarm"
    const val APPEAL_1 = "appeal1"
    const val APPEAL_2 = "appeal2"
    const val APPEAL_3 = "appeal3"
    const val DECLARATION = "declaration"
    const val DECLARATION_DETAIL_PATTERN = "declaration/{$ARG_POST_ID}/{$ARG_REPORT_ID}"
    const val ACCESSION = "accession"
    const val ACCESSION_DETAIL_PATTERN = "accessionDetail/{$ARG_EMAIL}"
    const val BAN_CLEAR = "banclear"
    const val BAN_CLEAR_DETAIL_PATTERN = "banclearDetail/{$ARG_EMAIL}"
    const val BAN_CLEAR_POST_PATTERN = "banclearPost/{$ARG_EMAIL}/{$ARG_POST_ID}"

    private val authEntryRoutes = setOf(
        ONBOARDING,
        LOGIN,
        MANAGER_LOGIN,
        INFO,
        WAIT,
        REFUSE,
        APPEAL_1,
        APPEAL_2,
        APPEAL_3
    )

    fun main(selectedTab: Int): String = "$MAIN?$ARG_SELECTED_TAB=$selectedTab"
    fun writeOpen(postId: Int): String = "writeOpen/$postId"
    fun writeOpen(postId: String): String = "writeOpen/$postId"
    fun answerOpen(id: Int): String = "answerOpen/$id"
    fun noticeOpen(id: Int): String = "notices/$id"
    fun threeWeekOpen(groupId: Int): String = "threeWeekOpen/$groupId"
    fun threeWeekDetail(groupId: Int, id: Int): String = "threeWeekDetail/$groupId/$id"
    fun declarationDetail(postId: Int, reportId: Int): String = "declaration/$postId/$reportId"
    fun accessionDetail(email: String): String = "accessionDetail/$email"
    fun banClearDetail(email: String): String = "banclearDetail/$email"
    fun banClearPost(email: String, postId: Int): String = "banclearPost/$email/$postId"

    fun forAuthSession(session: AuthSession): String? = when (session) {
        AuthSession.Checking -> null
        AuthSession.SignedOut -> LOGIN
        AuthSession.NeedsStudentInfo, AuthSession.Reapplying -> INFO
        AuthSession.Pending -> WAIT
        AuthSession.Rejected -> REFUSE
        AuthSession.Active, AuthSession.Warned, AuthSession.Admin -> MAIN
        is AuthSession.Blocked -> if (session.appealSubmitted) APPEAL_3 else APPEAL_1
    }

    fun forNotification(type: String?, id: String?): String? {
        val targetId = id?.toIntOrNull()?.takeIf { it > 0 } ?: return null
        return when (type) {
            "notice" -> noticeOpen(targetId)
            "answer" -> answerOpen(targetId)
            "post" -> writeOpen(targetId)
            else -> null
        }
    }

    fun isAuthEntry(route: String?): Boolean = route in authEntryRoutes

    fun notificationDestination(routePattern: String?, targetId: String?): String? {
        val id = targetId?.toIntOrNull()?.takeIf { it > 0 } ?: return null
        return when (routePattern) {
            NOTICE_OPEN_PATTERN -> noticeOpen(id)
            ANSWER_OPEN_PATTERN -> answerOpen(id)
            WRITE_OPEN_PATTERN -> writeOpen(id)
            else -> null
        }
    }
}

internal data class NotificationNavigationDecision(
    val consume: Boolean = false,
    val route: String? = null,
    val markRead: Boolean = false
)

internal fun notificationNavigationDecision(
    launch: NotificationLaunch?,
    handledKey: String?,
    isAuthenticated: Boolean,
    canOpenNotifications: Boolean,
    onboarding: Boolean,
    currentRoute: String?,
    currentTargetId: String?
): NotificationNavigationDecision {
    if (launch == null || handledKey == launch.key || !isAuthenticated) {
        return NotificationNavigationDecision()
    }
    if (!canOpenNotifications) {
        return NotificationNavigationDecision(consume = true)
    }
    if (onboarding || currentRoute == null || AppRoute.isAuthEntry(currentRoute)) {
        return NotificationNavigationDecision()
    }

    val targetRoute = launch.route ?: AppRoute.ALARM
    val currentConcreteRoute =
        AppRoute.notificationDestination(currentRoute, currentTargetId) ?: currentRoute
    return NotificationNavigationDecision(
        consume = true,
        route = targetRoute.takeUnless { it == currentConcreteRoute },
        markRead = true
    )
}

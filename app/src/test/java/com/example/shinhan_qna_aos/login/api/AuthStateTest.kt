package com.example.shinhan_qna_aos.login.api

import com.example.shinhan_qna_aos.AppRoute
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthStateTest {
    @Test
    fun existingUserStatusDestinationsRemainUnchanged() {
        assertEquals("info", AppRoute.forAuthSession(authSessionForUser("", false, false)))
        assertEquals("wait", AppRoute.forAuthSession(authSessionForUser("가입 대기 중", false, false)))
        assertEquals("refuse", AppRoute.forAuthSession(authSessionForUser("가입 거절", false, false)))
        assertEquals("refuse", AppRoute.forAuthSession(authSessionForUser("거절", false, false)))
        assertEquals("main", AppRoute.forAuthSession(authSessionForUser("가입 완료", true, false)))
        assertEquals("main", AppRoute.forAuthSession(authSessionForUser("경고", true, false)))
        assertEquals("appeal1", AppRoute.forAuthSession(authSessionForUser("차단", true, false)))
        assertEquals("appeal3", AppRoute.forAuthSession(authSessionForUser("차단", true, true)))
    }

    @Test
    fun reapplicationStaysOnFormUntilSubmissionCompletes() {
        assertEquals("info", AppRoute.forAuthSession(authSessionForUser("가입 거절", false, false, true)))
        assertEquals("info", AppRoute.forAuthSession(authSessionForUser("가입 대기 중", true, false, true)))
        assertEquals("main", AppRoute.forAuthSession(authSessionForUser("가입 완료", true, false, true)))
    }

    @Test
    fun adminUsesMainRouteWithoutChangingUserStatusMapping() {
        assertEquals("main", AppRoute.forAuthSession(AuthSession.Admin))
        assertEquals("login", AppRoute.forAuthSession(AuthSession.SignedOut))
        assertEquals(null, AppRoute.forAuthSession(AuthSession.Checking))
    }
}

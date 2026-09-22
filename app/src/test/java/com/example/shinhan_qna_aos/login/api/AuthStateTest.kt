package com.example.shinhan_qna_aos.login.api

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthStateTest {
    @Test
    fun existingUserStatusDestinationsRemainUnchanged() {
        assertEquals("info", routeForAuthSession(authSessionForUser("", false, false)))
        assertEquals("wait", routeForAuthSession(authSessionForUser("가입 대기 중", false, false)))
        assertEquals("refuse", routeForAuthSession(authSessionForUser("가입 거절", false, false)))
        assertEquals("refuse", routeForAuthSession(authSessionForUser("거절", false, false)))
        assertEquals("main", routeForAuthSession(authSessionForUser("가입 완료", true, false)))
        assertEquals("main", routeForAuthSession(authSessionForUser("경고", true, false)))
        assertEquals("appeal1", routeForAuthSession(authSessionForUser("차단", true, false)))
        assertEquals("appeal3", routeForAuthSession(authSessionForUser("차단", true, true)))
    }

    @Test
    fun reapplicationStaysOnFormUntilSubmissionCompletes() {
        assertEquals("info", routeForAuthSession(authSessionForUser("가입 거절", false, false, true)))
        assertEquals("info", routeForAuthSession(authSessionForUser("가입 대기 중", true, false, true)))
        assertEquals("main", routeForAuthSession(authSessionForUser("가입 완료", true, false, true)))
    }

    @Test
    fun adminUsesMainRouteWithoutChangingUserStatusMapping() {
        assertEquals("main", routeForAuthSession(AuthSession.Admin))
        assertEquals("login", routeForAuthSession(AuthSession.SignedOut))
        assertEquals(null, routeForAuthSession(AuthSession.Checking))
    }
}

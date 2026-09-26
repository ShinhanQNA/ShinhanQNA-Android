package com.example.shinhan_qna_aos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppRouteTest {
    @Test
    fun buildsDynamicRoutes() {
        assertEquals("main?selectedTab=2", AppRoute.main(2))
        assertEquals("writeOpen/3", AppRoute.writeOpen(3))
        assertEquals("answerOpen/4", AppRoute.answerOpen(4))
        assertEquals("notices/5", AppRoute.noticeOpen(5))
        assertEquals("threeWeekOpen/6", AppRoute.threeWeekOpen(6))
        assertEquals("threeWeekDetail/6/7", AppRoute.threeWeekDetail(6, 7))
        assertEquals("declaration/8/9", AppRoute.declarationDetail(8, 9))
        assertEquals("accessionDetail/student", AppRoute.accessionDetail("student"))
        assertEquals("banclearDetail/student", AppRoute.banClearDetail("student"))
        assertEquals("banclearPost/student/10", AppRoute.banClearPost("student", 10))
    }

    @Test
    fun recognizesAuthEntryRoutes() {
        assertTrue(AppRoute.isAuthEntry(AppRoute.ONBOARDING))
        assertTrue(AppRoute.isAuthEntry(AppRoute.WAIT))
        assertTrue(AppRoute.isAuthEntry(AppRoute.APPEAL_1))
        assertFalse(AppRoute.isAuthEntry(AppRoute.MAIN_PATTERN))
    }
}

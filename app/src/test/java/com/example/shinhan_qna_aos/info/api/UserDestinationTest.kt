package com.example.shinhan_qna_aos.info.api

import org.junit.Assert.assertEquals
import org.junit.Test

class UserDestinationTest {
    @Test
    fun rejectedUserSeesRefusalEvenWhenStudentCertificationIsFalse() {
        assertEquals("refuse", destinationForUserStatus("가입 거절", false, false))
        assertEquals("refuse", destinationForUserStatus("거절", false, false))
    }

    @Test
    fun blockedUserWithCompletedAppealSeesPendingScreen() {
        assertEquals("appeal3", destinationForUserStatus("차단", true, true))
    }

    @Test
    fun pendingServerStatusTakesPriorityOverLocalCertificationFlag() {
        assertEquals("wait", destinationForUserStatus("가입 대기 중", false, false))
    }

    @Test
    fun reapplicationStaysOnFormUntilStudentInfoIsSubmitted() {
        assertEquals("info", destinationForUserStatus("가입 대기 중", true, false, true))
        assertEquals("info", destinationForUserStatus("가입 거절", false, false, true))
        assertEquals("main", destinationForUserStatus("가입 완료", true, false, true))
    }
}

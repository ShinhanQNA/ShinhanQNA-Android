package com.example.shinhan_qna_aos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PushNotificationRouteTest {
    @Test
    fun routesKnownNotificationTypes() {
        assertEquals("notices/4", AppRoute.forNotification("notice", "4"))
        assertEquals("answerOpen/5", AppRoute.forNotification("answer", "5"))
        assertEquals("writeOpen/6", AppRoute.forNotification("post", "6"))
    }

    @Test
    fun ignoresMissingOrInvalidTargets() {
        assertNull(AppRoute.forNotification("notice", "bad"))
        assertNull(AppRoute.forNotification("answer", null))
        assertNull(AppRoute.forNotification("post", ""))
        assertNull(AppRoute.forNotification("post", "bad/id"))
        assertNull(AppRoute.forNotification("post", "0"))
        assertNull(AppRoute.forNotification("notice", "-1"))
    }

    @Test
    fun recognizesCurrentNotificationDestination() {
        assertEquals("notices/4", AppRoute.notificationDestination(AppRoute.NOTICE_OPEN_PATTERN, "4"))
        assertEquals("answerOpen/5", AppRoute.notificationDestination(AppRoute.ANSWER_OPEN_PATTERN, "5"))
        assertEquals("writeOpen/6", AppRoute.notificationDestination(AppRoute.WRITE_OPEN_PATTERN, "6"))
        assertNull(AppRoute.notificationDestination(AppRoute.MAIN_PATTERN, "6"))
    }

    @Test
    fun waitsForAuthRouteThenConsumesNotificationOnce() {
        val launch = NotificationLaunch("message-1", AppRoute.noticeOpen(4))

        assertEquals(
            NotificationNavigationDecision(),
            notificationNavigationDecision(launch, null, true, true, false, AppRoute.WAIT, null)
        )
        assertEquals(
            NotificationNavigationDecision(consume = true, route = AppRoute.noticeOpen(4), markRead = true),
            notificationNavigationDecision(launch, null, true, true, false, AppRoute.MAIN_PATTERN, null)
        )
        assertEquals(
            NotificationNavigationDecision(),
            notificationNavigationDecision(launch, launch.key, true, true, false, AppRoute.MAIN_PATTERN, null)
        )
    }

    @Test
    fun doesNotStackCurrentNotificationDestination() {
        val launch = NotificationLaunch("message-2", AppRoute.answerOpen(5))

        assertEquals(
            NotificationNavigationDecision(consume = true, markRead = true),
            notificationNavigationDecision(
                launch,
                null,
                true,
                true,
                false,
                AppRoute.ANSWER_OPEN_PATTERN,
                "5"
            )
        )
    }

    @Test
    fun consumesNotificationWhenCurrentAuthStateCannotOpenIt() {
        val launch = NotificationLaunch("message-3", AppRoute.writeOpen(6))

        assertEquals(
            NotificationNavigationDecision(consume = true),
            notificationNavigationDecision(launch, null, true, false, false, AppRoute.WAIT, null)
        )
    }
}

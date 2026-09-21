package com.example.shinhan_qna_aos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PushNotificationRouteTest {
    @Test
    fun routesKnownNotificationTypes() {
        assertEquals("notices/4", routeForNotification("notice", "4"))
        assertEquals("answerOpen/5", routeForNotification("answer", "5"))
        assertEquals("writeOpen/6", routeForNotification("post", "6"))
    }

    @Test
    fun ignoresMissingOrInvalidTargets() {
        assertNull(routeForNotification("notice", "bad"))
        assertNull(routeForNotification("answer", null))
        assertNull(routeForNotification("post", ""))
        assertNull(routeForNotification("post", "bad/id"))
        assertNull(routeForNotification("post", "0"))
        assertNull(routeForNotification("notice", "-1"))
    }
}

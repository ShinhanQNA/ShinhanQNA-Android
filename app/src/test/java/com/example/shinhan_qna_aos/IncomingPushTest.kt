package com.example.shinhan_qna_aos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IncomingPushTest {
    @Test
    fun readsOldBranchPayloadAndBackgroundNotificationExtras() {
        val data = incomingPush(mapOf("type" to "post", "postId" to "12", "title" to "새 글"))
        assertEquals("post", data?.type)
        assertEquals("12", data?.targetId)
        assertEquals("새 글", data?.title)

        val background = incomingPush(mapOf(
            "google.message_id" to "message-1",
            "gcm.notification.title" to "공지",
            "gcm.notification.body" to "내용",
            "type" to "notice",
            "postId" to "3"
        ))
        assertEquals("message-1", background?.key)
        assertEquals("공지", background?.title)
        assertEquals("내용", background?.body)
    }

    @Test
    fun ignoresPlainLauncherIntent() {
        assertNull(incomingPush(emptyMap()))
    }
}

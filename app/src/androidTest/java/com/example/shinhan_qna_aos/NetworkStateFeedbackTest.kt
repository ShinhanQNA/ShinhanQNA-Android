package com.example.shinhan_qna_aos

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NetworkStateFeedbackTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun hidesFeedbackWhenIdle() {
        composeRule.setContent {
            NetworkStateFeedback(isLoading = false, errorMessage = null)
        }

        assertTrue(composeRule.onAllNodesWithText("다시 시도").fetchSemanticsNodes().isEmpty())
        assertTrue(
            composeRule.onAllNodes(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
                .fetchSemanticsNodes().isEmpty()
        )
    }

    @Test
    fun showsErrorAndInvokesRetry() {
        var retried = false
        composeRule.setContent {
            NetworkStateFeedback(
                isLoading = false,
                errorMessage = "로드 실패",
                onRetry = { retried = true }
            )
        }

        composeRule.onNodeWithText("로드 실패").assertIsDisplayed()
        composeRule.onNodeWithText("다시 시도").performClick()
        composeRule.runOnIdle { assertTrue(retried) }
    }

    @Test
    fun showsLoadingWithoutRetry() {
        composeRule.setContent {
            NetworkStateFeedback(isLoading = true, errorMessage = null)
        }

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
        assertTrue(composeRule.onAllNodesWithText("다시 시도").fetchSemanticsNodes().isEmpty())
    }
}

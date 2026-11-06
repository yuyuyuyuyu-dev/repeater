package dev.yuyuyuyuyu.repeater.spec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.repeater.App
import web.clipboard.readText
import web.navigator.navigator
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class CopyToClipboardTest {
    @Test
    fun `should copy Kotlin repeated within 140 characters when Kotlin is typed right after opening the app`() =
        runComposeUiTest {
            // Arrange
            setContent { App() }

            // Act
            onNodeWithText("繰り返したい文字列を入力してください").performTextInput("Kotlin")
            onNodeWithText("クリップボードにコピー").performClick()
            awaitIdle()
            val clipboardText = navigator.clipboard.readText()
            awaitIdle()

            // Assert
            assertEquals("Kotlin".repeat(23), clipboardText)
            onNodeWithText("コピーしました").assertIsDisplayed()
        }
}

package dev.yuyuyuyuyu.repeater.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.repeater.App
import web.clipboard.readText
import web.clipboard.writeText
import web.navigator.navigator
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class LongInputTest {
    @Test
    fun `should copy nothing when the input is longer than 140 characters`() =
        runComposeUiTest {
            // Arrange
            navigator.clipboard.writeText("not copied yet")
            setContent { App() }

            // Act
            onNodeWithText("繰り返したい文字列を入力してください").performTextInput("Kotlin".repeat(24))
            onNodeWithText("クリップボードにコピー").performClick()
            awaitIdle()
            val clipboardText = navigator.clipboard.readText()

            // Assert
            assertEquals("", clipboardText)
        }

    @Test
    fun `should copy the first 140 characters of a longer input when the cram switch is on`() =
        runComposeUiTest {
            // Arrange
            setContent { App() }

            // Act
            onNodeWithText("詳細設定").performClick()
            onNodeWithText("上限いっぱいまで文字を詰め込む").performClick()
            onNodeWithText("繰り返したい文字列を入力してください").performTextInput("Kotlin".repeat(24))
            onNodeWithText("クリップボードにコピー").performClick()
            awaitIdle()
            val clipboardText = navigator.clipboard.readText()

            // Assert
            assertEquals("Kotlin".repeat(24).take(140), clipboardText)
        }
}

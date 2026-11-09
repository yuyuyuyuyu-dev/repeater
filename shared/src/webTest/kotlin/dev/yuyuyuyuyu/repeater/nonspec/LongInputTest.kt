package dev.yuyuyuyuyu.repeater.nonspec

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
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
    fun `should disable the copy button when the input is longer than 140 characters`() =
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
            onNodeWithText("クリップボードにコピー").assertIsNotEnabled()
            assertEquals("not copied yet", clipboardText)
        }

    @Test
    fun `should disable the copy button for a longer input even when the cram switch is on`() =
        runComposeUiTest {
            // Arrange
            navigator.clipboard.writeText("not copied yet")
            setContent { App() }

            // Act
            onNodeWithText("詳細設定").performClick()
            onNodeWithText("上限いっぱいまで文字を詰め込む").performClick()
            onNodeWithText("繰り返したい文字列を入力してください").performTextInput("Kotlin".repeat(24))
            onNodeWithText("クリップボードにコピー").performClick()
            awaitIdle()
            val clipboardText = navigator.clipboard.readText()

            // Assert
            onNodeWithText("クリップボードにコピー").assertIsNotEnabled()
            assertEquals("not copied yet", clipboardText)
        }

    @Test
    fun `should copy an input of exactly 140 characters as it is`() =
        runComposeUiTest {
            // Arrange
            setContent { App() }

            // Act
            onNodeWithText("繰り返したい文字列を入力してください")
                .performTextInput("Kotlin".repeat(23) + "Ko")
            onNodeWithText("クリップボードにコピー").performClick()
            awaitIdle()
            val clipboardText = navigator.clipboard.readText()

            // Assert
            assertEquals("Kotlin".repeat(23) + "Ko", clipboardText)
        }

    @Test
    fun `should copy an input longer than 140 characters when the slider is raised above it`() =
        runComposeUiTest {
            // Arrange
            setContent { App() }

            // Act
            onNodeWithText("詳細設定").performClick()
            onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress)).requestFocus()
            onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress))
                .performKeyInput { pressKey(Key.MoveEnd) }
            onNodeWithText("繰り返したい文字列を入力してください").performTextInput("Kotlin".repeat(24))
            onNodeWithText("クリップボードにコピー").performClick()
            awaitIdle()
            val clipboardText = navigator.clipboard.readText()

            // Assert
            assertEquals("Kotlin".repeat(72), clipboardText)
        }
}

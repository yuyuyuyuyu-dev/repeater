package dev.yuyuyuyuyu.repeater.nonspec

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.repeater.App
import web.clipboard.readText
import web.navigator.navigator
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class AdvancedSettingsTest {
    @Test
    fun `should copy Kotlin crammed into 140 characters when the cram switch is turned on`() =
        runComposeUiTest {
            // Arrange
            setContent { App() }

            // Act
            onNodeWithText("詳細設定").performClick()
            onNodeWithText("上限いっぱいまで文字を詰め込む").performClick()
            onNodeWithText("繰り返したい文字列を入力してください").performTextInput("Kotlin")
            onNodeWithText("クリップボードにコピー").performClick()
            awaitIdle()
            val clipboardText = navigator.clipboard.readText()

            // Assert
            assertEquals("Kotlin".repeat(23) + "Ko", clipboardText)
        }

    @Test
    fun `should copy Kotlin repeated within 500 characters when the slider is moved to the end with the keyboard`() =
        runComposeUiTest {
            // Arrange
            setContent { App() }

            // Act
            onNodeWithText("詳細設定").performClick()
            onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress)).requestFocus()
            onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.SetProgress))
                .performKeyInput { pressKey(Key.MoveEnd) }
            onNodeWithText("繰り返したい文字列を入力してください").performTextInput("Kotlin")
            onNodeWithText("クリップボードにコピー").performClick()
            awaitIdle()
            val clipboardText = navigator.clipboard.readText()

            // Assert
            assertEquals("Kotlin".repeat(83), clipboardText)
        }
}

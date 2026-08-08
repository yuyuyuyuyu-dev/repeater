package dev.yuyuyuyuyu.repeater

import kotlin.test.Test
import kotlin.test.assertEquals

class RepeatTest {
    @Test
    fun `空文字列を渡すと空文字列を返す`() {
        // Act
        val actual = repeat("", 140, false)

        // Assert
        assertEquals("", actual)
    }

    @Test
    fun `上限いっぱいまで詰め込む場合でも空文字列を渡すと空文字列を返す`() {
        // Act
        val actual = repeat("", 140, true)

        // Assert
        assertEquals("", actual)
    }

    @Test
    fun `上限を超えない範囲で繰り返す`() {
        // Act
        val actual = repeat("いちご", 140, false)

        // Assert
        assertEquals("いちご".repeat(46), actual)
    }

    @Test
    fun `上限いっぱいまで詰め込む`() {
        // Act
        val actual = repeat("いちご", 140, true)

        // Assert
        assertEquals("いちご".repeat(46) + "いち", actual)
    }
}

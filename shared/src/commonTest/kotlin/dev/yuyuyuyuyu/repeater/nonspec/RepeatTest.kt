package dev.yuyuyuyuyu.repeater.nonspec

import dev.yuyuyuyuyu.repeater.repeat
import kotlin.test.Test
import kotlin.test.assertEquals

class RepeatTest {
    @Test
    fun `should return an empty string for an empty string`() {
        // Act
        val actual = repeat("", 140, false)

        // Assert
        assertEquals("", actual)
    }

    @Test
    fun `should return an empty string for an empty string even when cramming to the max`() {
        // Act
        val actual = repeat("", 140, true)

        // Assert
        assertEquals("", actual)
    }

    @Test
    fun `should repeat without exceeding the max length`() {
        // Act
        val actual = repeat("いちご", 140, false)

        // Assert
        assertEquals("いちご".repeat(46), actual)
    }

    @Test
    fun `should cram up to the max length`() {
        // Act
        val actual = repeat("いちご", 140, true)

        // Assert
        assertEquals("いちご".repeat(46) + "いち", actual)
    }
}

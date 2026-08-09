package dev.yuyuyuyuyu.repeater

import web.clipboard.writeText
import web.navigator.navigator

actual suspend fun copyToClipboard(text: String) {
    navigator.clipboard.writeText(text)
}

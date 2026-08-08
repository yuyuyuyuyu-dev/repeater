package dev.yuyuyuyuyu.repeater

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual suspend fun copyToClipboard(text: String) {
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
}

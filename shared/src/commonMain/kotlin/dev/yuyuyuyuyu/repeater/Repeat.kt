package dev.yuyuyuyuyu.repeater

fun repeat(
    repeatText: String,
    resultMaxLength: Int,
    cramToTheMax: Boolean,
): String {
    if (repeatText.isEmpty()) {
        return ""
    }

    val times = resultMaxLength / repeatText.length

    return if (cramToTheMax) {
        repeatText.repeat(times + 1).take(resultMaxLength)
    } else {
        repeatText.repeat(times)
    }
}

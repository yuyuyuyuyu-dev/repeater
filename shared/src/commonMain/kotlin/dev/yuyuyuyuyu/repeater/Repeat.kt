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

    if (cramToTheMax) {
        return repeatText.repeat(times + 1).take(resultMaxLength)
    }

    return repeatText.repeat(times)
}

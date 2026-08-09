package dev.yuyuyuyuyu.repeater

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val MinMaxLength = 20
private const val MaxMaxLength = 500
private const val MaxLengthStep = 20
private const val DefaultMaxLength = 140

// Matches the width of Container maxWidth="xs" in the MUI version
private val ContentMaxWidth = 444.dp

private val MaxLengthMarks = listOf(140, 280)

@Composable
@Preview
fun App() {
    RepeaterTheme {
        var unprocessedText by remember { mutableStateOf("") }
        var processedTextMaxLength by remember { mutableIntStateOf(DefaultMaxLength) }
        var cramToTheMax by remember { mutableStateOf(false) }
        val processedText = remember(unprocessedText, processedTextMaxLength, cramToTheMax) {
            repeat(unprocessedText, processedTextMaxLength, cramToTheMax)
        }

        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .safeContentPadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = ContentMaxWidth)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = unprocessedText,
                        onValueChange = { unprocessedText = it },
                        label = { Text("繰り返したい文字列を入力してください") },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    AdvancedSettings(
                        cramToTheMax = cramToTheMax,
                        onCramToTheMaxChange = { cramToTheMax = it },
                        maxLength = processedTextMaxLength,
                        onMaxLengthChange = { processedTextMaxLength = it },
                    )

                    Text(processedText)

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val message = try {
                                    copyToClipboard(processedText)
                                    "コピーしました"
                                } catch (cancellation: CancellationException) {
                                    throw cancellation
                                } catch (throwable: Throwable) {
                                    // The browser rejects the write without permission or a
                                    // secure context. Letting it escape cancels the composition
                                    // scope, which freezes the whole screen.
                                    "コピーできませんでした"
                                }

                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("クリップボードにコピー")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdvancedSettings(
    cramToTheMax: Boolean,
    onCramToTheMaxChange: (Boolean) -> Unit,
    maxLength: Int,
    onMaxLengthChange: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val expandIconRotation by animateFloatAsState(if (expanded) 180f else 0f)

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "詳細設定",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = ExpandMoreIcon,
                contentDescription = if (expanded) "詳細設定を閉じる" else "詳細設定を開く",
                modifier = Modifier.rotate(expandIconRotation),
            )
        }

        AnimatedVisibility(expanded) {
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = cramToTheMax,
                            role = Role.Switch,
                            onValueChange = onCramToTheMaxChange,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(checked = cramToTheMax, onCheckedChange = null)
                    Spacer(Modifier.width(8.dp))
                    Text("上限いっぱいまで文字を詰め込む")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("文字数", modifier = Modifier.weight(1f))
                    Text("${maxLength}文字", style = MaterialTheme.typography.labelLarge)
                }

                Slider(
                    value = maxLength.toFloat(),
                    onValueChange = { onMaxLengthChange(it.roundToInt()) },
                    valueRange = MinMaxLength.toFloat()..MaxMaxLength.toFloat(),
                    steps = (MaxMaxLength - MinMaxLength) / MaxLengthStep - 1,
                    modifier = Modifier.fillMaxWidth(),
                )

                SliderMarks(
                    marks = MaxLengthMarks,
                    valueRange = MinMaxLength.toFloat()..MaxMaxLength.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SliderMarks(
    marks: List<Int>,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            marks.forEach { mark ->
                Text("${mark}文字", style = MaterialTheme.typography.labelSmall)
            }
        },
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
        val width = constraints.maxWidth
        val height = placeables.maxOfOrNull { it.height } ?: 0

        layout(width, height) {
            placeables.forEachIndexed { index, placeable ->
                val fraction =
                    (marks[index] - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                val center = (width * fraction).roundToInt()
                placeable.place(
                    x = (center - placeable.width / 2).coerceIn(0, width - placeable.width),
                    y = 0,
                )
            }
        }
    }
}

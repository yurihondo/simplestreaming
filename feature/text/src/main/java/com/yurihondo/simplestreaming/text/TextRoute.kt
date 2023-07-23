package com.yurihondo.simplestreaming.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun TextRoute(
    modifier: Modifier = Modifier,
    viewModel: TextRouteViewModel,
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isStreaming by viewModel.isStreaming.collectAsStateWithLifecycle()
    TextScreen(
        isLoggedIn = isLoggedIn,
        isInStreaming = isStreaming,
        onClickStartStreaming = viewModel::onStartStreaming,
        onClickStopStreaming = {},
        onUpdateStreamingText = viewModel::onUpdateStreamingText,
        onInputtedTextChange = viewModel::onInputtedTextUpdated,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextScreen(
    isLoggedIn: Boolean,
    isInStreaming: Boolean,
    onClickStartStreaming: () -> Unit,
    onClickStopStreaming: () -> Unit,
    onUpdateStreamingText: () -> Unit,
    onInputtedTextChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                text = "Text streaming",
                style = MaterialTheme.typography.displaySmall,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(""))
            }
            TextField(
                value = text,
                onValueChange = { new ->
                    text = new
                    onInputtedTextChange(new)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                placeholder = {
                    Text(
                        text = "Enter text to stream",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isInStreaming) {
                Row {
                    val contentSize = remember { DpSize(150.dp, 48.dp) }
                    Button(
                        modifier = Modifier.size(contentSize),
                        onClick = onUpdateStreamingText,
                    ) {
                        Text(
                            text = "Update text",
                            maxLines = 1,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        modifier = Modifier.size(contentSize),
                        onClick = onClickStopStreaming,
                    ) {
                        Text(
                            text = "Stop Streaming",
                            maxLines = 1,
                        )
                    }
                }
            } else {
                Button(
                    onClick = onClickStartStreaming,
                    enabled = isLoggedIn,
                ) {
                    Text(text = "Start Streaming")
                }
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreviewTextRoute() {
    MaterialTheme {
        TextScreen(
            isLoggedIn = true,
            isInStreaming = true,
            onClickStartStreaming = {},
            onClickStopStreaming = {},
            onUpdateStreamingText = {},
            onInputtedTextChange = {},
        )
    }
}
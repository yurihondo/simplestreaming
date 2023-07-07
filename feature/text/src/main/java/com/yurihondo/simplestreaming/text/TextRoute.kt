package com.yurihondo.simplestreaming.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun TextRoute(
    modifier: Modifier = Modifier,
    viewModel: TextRouteViewModel,
) {
    TextScreen(
        onClickStartStreaming = viewModel::onStartStreaming,
        onClickStopStreaming = {},
        modifier = modifier,
    )
}

@Composable
private fun TextScreen(
    onClickStartStreaming: () -> Unit,
    onClickStopStreaming: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Text")
        Button(onClick = onClickStartStreaming) {
            Text(text = "Start Streaming")
        }
        Button(onClick = onClickStopStreaming) {
            Text(text = "Stop Streaming")
        }
    }
}

@Preview
@Composable
private fun DefaultPreviewTextRoute() {
    MaterialTheme {
        TextScreen(
            onClickStartStreaming = {},
            onClickStopStreaming = {},
        )
    }
}
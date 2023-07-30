package com.yurihondo.simplestreaming.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yurihondo.simplestreaming.core.ui.LocalBottomNavigationManager
import com.yurihondo.simplestreaming.core.ui.text.HyperlinkText
import kotlinx.coroutines.delay
import com.yurihondo.simplestreaming.core.ui.R as CoreR

@Composable
internal fun WelcomeRoute(
    viewModel: WelcomeRouteViewModel,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomNavigationManager = LocalBottomNavigationManager.current
    DisposableEffect(Unit) {
        bottomNavigationManager.hideBottomSheet()
        onDispose { }
    }

    val acceptedTermsOfUse by viewModel.acceptedTermsOfUse.collectAsStateWithLifecycle()
    WelcomeScreen(
        acceptedTermsOfUse = acceptedTermsOfUse,
        navigateToHome = navigateToHome,
        onClickAccept = viewModel::onAcceptTermsOfUse,
        modifier = modifier,
    )
}

@Composable
private fun WelcomeScreen(
    acceptedTermsOfUse: Boolean?,
    navigateToHome: () -> Unit,
    onClickAccept: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(acceptedTermsOfUse) {
        if (acceptedTermsOfUse == true) {
            delay(500) // to prevent flickering
            navigateToHome()
        }
    }

    when (acceptedTermsOfUse) {
        false -> WelcomeContent(
            onClickAccept = onClickAccept,
            modifier = modifier,
        )

        else -> WelcomeLoading()
    }
}

@Composable
fun WelcomeLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        //progress indicator
        CircularProgressIndicator()
        Text(text = "Starting...")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WelcomeContent(
    onClickAccept: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    var isAcceptButtonEnable by rememberSaveable { mutableStateOf(false) }
    val isAtEnd = remember {
        derivedStateOf {
            val lastVisibleItem = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true
            lastVisibleItem.index == scrollState.layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(isAtEnd) {
        snapshotFlow { isAtEnd.value }
            .collect {
                if (it) isAcceptButtonEnable = true
            }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Welcome",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = onClickAccept, enabled = isAcceptButtonEnable) {
                    Text("Accept")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, top = 48.dp, end = 16.dp),
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = "Terms of use",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                HyperlinkText(
                    resourceId = CoreR.string.terms_of_use_txt,
                )
            }
        }
    }
}


@Preview
@Composable
private fun DefaultPreviewWelcomeLoading() {
    MaterialTheme {
        WelcomeLoading()
    }
}

@Preview
@Composable
private fun DefaultPreviewWelcomeScreen() {
    MaterialTheme {
        WelcomeContent(
            onClickAccept = {},
        )
    }
}
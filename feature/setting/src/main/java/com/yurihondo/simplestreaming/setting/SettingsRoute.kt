package com.yurihondo.simplestreaming.setting

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
) {
    val accountName by viewModel.accountName.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    SettingsScreen(
        modifier = modifier,
        listState = rememberLazyListState(),
        accountName = accountName,
        isLoggedIn = isLoggedIn,
        onClickLogin = viewModel::onStartLogin,
    )
}

@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    accountName: String,
    isLoggedIn: Boolean,
    onClickLogin: () -> Unit,
) {
    SettingsContent(
        modifier = modifier,
        listState = listState,
        accountName = accountName,
        isLoggedIn = isLoggedIn,
        onClickLogin = onClickLogin,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    accountName: String,
    isLoggedIn: Boolean,
    onClickLogin: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            state = listState,
        ) {
            item {
                AccountCard(
                    name = accountName,
                    isLoggedIn = isLoggedIn,
                    onClickLogin = onClickLogin,
                )
            }
        }
    }
}

@Composable
private fun AccountCard(
    name: String,
    isLoggedIn: Boolean,
    onClickLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(48.dp),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            imageVector = Icons.Default.NoAccounts,
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = name,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!isLoggedIn) {
            Button(onClick = onClickLogin) {
                Text(text = "Login", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSettingsContent() {
    MaterialTheme {
        SettingsContent(
            listState = rememberLazyListState(),
            accountName = "",
            isLoggedIn = false,
            onClickLogin = {}
        )
    }
}

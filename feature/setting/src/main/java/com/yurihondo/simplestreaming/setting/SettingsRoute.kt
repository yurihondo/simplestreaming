package com.yurihondo.simplestreaming.setting

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yurihondo.simplestreaming.core.ui.HyperlinkText

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
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            item {
                AccountCard(
                    name = accountName,
                    isLoggedIn = isLoggedIn,
                    onClickLogin = onClickLogin,
                )
            }

            simpleMessageDialogItem(
                itemName = "Terms of use",
                dialogMessageResId = R.string.terms_of_use_txt,
            )

            simpleMessageDialogItem(
                itemName = "Privacy policy",
                dialogMessageResId = R.string.privacy_policy_txt,
            )
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

private fun LazyListScope.simpleMessageDialogItem(
    itemName: String,
    @StringRes dialogMessageResId: Int,
) {
    item {
        var isShowing by rememberSaveable { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { isShowing = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = itemName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = itemName
            )
        }

        if (isShowing) {
            Dialog(
                onDismissRequest = { isShowing = false }
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = itemName,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HyperlinkText(resourceId = dialogMessageResId)
                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { isShowing = false }
                    ) {
                        Text(text = "OK")
                    }
                }
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

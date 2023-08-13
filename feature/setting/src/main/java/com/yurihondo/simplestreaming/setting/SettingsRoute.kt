package com.yurihondo.simplestreaming.setting

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yurihondo.simplestreaming.core.ui.text.HyperlinkText
import com.yurihondo.simplestreaming.core.ui.R as CoreR

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onClickLicenseItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accountName by viewModel.accountName.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    SettingsScreen(
        modifier = modifier,
        listState = rememberLazyListState(),
        accountName = accountName,
        isLoggedIn = isLoggedIn,
        onClickLogin = viewModel::onStartLogin,
        onClickLicenseItem = onClickLicenseItem
    )
}

@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    accountName: String,
    isLoggedIn: Boolean,
    onClickLogin: () -> Unit,
    onClickLicenseItem: () -> Unit,
) {
    SettingsContent(
        modifier = modifier,
        listState = listState,
        accountName = accountName,
        isLoggedIn = isLoggedIn,
        onClickLogin = onClickLogin,
        onClickLicenseItem = onClickLicenseItem,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    accountName: String,
    isLoggedIn: Boolean,
    onClickLogin: () -> Unit,
    onClickLicenseItem: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, top = 48.dp, end = 16.dp),
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
                dialogMessageResId = CoreR.string.terms_of_use_txt,
            )

            simpleMessageDialogItem(
                itemName = "Privacy policy",
                dialogMessageResId = CoreR.string.privacy_policy_txt,
            )

            simpleSettingItem(
                itemName = "License",
                onClick = onClickLicenseItem,
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
            .clickable(onClick = onClickLogin)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(48.dp),
            contentDescription = null,
            contentScale = Crop,
            imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.Default.NoAccounts,
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = if (isLoggedIn) name else "Touch to login",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier.width(128.dp),
            painter = painterResource(id = R.drawable.yt_logo_rgb_dark),
            contentDescription = "YouTube",
        )
    }
}

private fun LazyListScope.simpleSettingItem(
    itemName: String,
    onClick: () -> Unit,
) {
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(onClick = onClick),
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
            onClickLogin = {},
            onClickLicenseItem = {},
        )
    }
}

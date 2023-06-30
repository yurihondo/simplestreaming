package com.yurihondo.simplestreaming.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yurihondo.simplestreaming.navigation.TopLevelDestination

@Composable
fun SimpleStreamingBottomBar(
    destinations: List<TopLevelDestination>,
    onClickItem: (TopLevelDestination) -> Unit,
    currentDestination: TopLevelDestination?,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            val selected = destination == currentDestination
            SimpleStreamingBottomBarItem(
                selected = selected,
                onClick = { onClickItem(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon.default,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.icon.selected,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.titleTextId),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

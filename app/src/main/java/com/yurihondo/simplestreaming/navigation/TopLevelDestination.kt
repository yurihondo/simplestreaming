package com.yurihondo.simplestreaming.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.yurihondo.simplestreaming.R

@Immutable
enum class TopLevelDestination(
    @StringRes val titleTextId: Int,
    val icon: DestinationIcon,
) {
    TEXT(
        titleTextId = R.string.destination_name_text,
        icon = DestinationIcon(
            default = Icons.Default.Title,
            selected = Icons.Default.TextFields,
        ),
    ),
    SETTINGS(
        titleTextId = R.string.destination_name_settings,
        icon = DestinationIcon(
            default = Icons.Outlined.Settings,
            selected = Icons.Filled.Settings,
        ),
    )
}

data class DestinationIcon(
    val default: ImageVector,
    val selected: ImageVector,
)

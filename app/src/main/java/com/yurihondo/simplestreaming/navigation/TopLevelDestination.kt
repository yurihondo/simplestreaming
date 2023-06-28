package com.yurihondo.simplestreaming.navigation

import androidx.annotation.StringRes
import com.yurihondo.simplestreaming.R

enum class TopLevelDestination(
    @StringRes val titleTextId: Int
) {
    TEXT(R.string.destination_name_text),
    SETTINGS(R.string.destination_name_settings),
}
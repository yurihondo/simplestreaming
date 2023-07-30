package com.yurihondo.simplestreaming.core.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.yurihondo.simplestreaming.core.ui.bottomnavigation.BottomNavigationManager

val LocalBottomNavigationManager = staticCompositionLocalOf<BottomNavigationManager> {
    error("No current BottomNavigationManager")
}
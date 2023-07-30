package com.yurihondo.simplestreaming.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.yurihondo.simplestreaming.core.ui.LocalBottomNavigationManager
import com.yurihondo.simplestreaming.navigation.MainNavHost

@Composable
fun SimpleStreamingApp(
    appState: AppState = rememberAppState(
        navHostController = rememberNavController(),
    ),
) {
    CompositionLocalProvider(
        LocalBottomNavigationManager provides appState,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                bottomBar = {
                    if (appState.shouldShowBottomNavBar) {
                        SimpleStreamingBottomBar(
                            destinations = appState.topLevelDestinations,
                            onClickItem = appState::navigateToTopLevelDestination,
                            currentDestination = appState.currentTopLevelDestination,
                        )
                    }
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
            ) { paddingValues ->
                MainNavHost(
                    navHostController = appState.navHostController,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}
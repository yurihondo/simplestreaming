package com.yurihondo.simplestreaming.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.yurihondo.simplestreaming.setting.navigation.settingGraph
import com.yurihondo.simplestreaming.text.navigation.navigateToTextGraph
import com.yurihondo.simplestreaming.text.navigation.textGraph
import com.yurihondo.simplestreaming.welcome.navigation.welcomeGraph
import com.yurihondo.simplestreaming.welcome.navigation.welcomeGraphRoutePattern

@Composable
fun MainNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = welcomeGraphRoutePattern,
        modifier = modifier,
    ) {
        welcomeGraph(
            navController = navHostController,
            onNavigateToHome = navHostController::navigateToTextGraph
        )
        textGraph(
            navController = navHostController
        )
        settingGraph(
            navController = navHostController
        )
    }
}
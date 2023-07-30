package com.yurihondo.simplestreaming.welcome.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.yurihondo.simplestreaming.welcome.WelcomeRoute

const val welcomeNavigationRoute = "welcome_route"
const val welcomeGraphRoutePattern = "welcome_graph"

fun NavGraphBuilder.welcomeGraph(
    navController: NavController,
    onNavigateToHome: () -> Unit,
) {
    navigation(
        route = welcomeGraphRoutePattern,
        startDestination = welcomeNavigationRoute,
    ) {
        welcomeRoute(
            navigateToHome = onNavigateToHome
        )
    }
}

internal fun NavGraphBuilder.welcomeRoute(
    navigateToHome: () -> Unit,
) {
    composable(route = welcomeNavigationRoute) {
        WelcomeRoute(
            viewModel = hiltViewModel(),
            navigateToHome = navigateToHome,
        )
    }
}

package com.yurihondo.simplestreaming.text.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.yurihondo.simplestreaming.text.TextRoute

const val textNavigationRoute = "text_route"
const val textGraphRoutePattern = "text_graph"

fun NavController.navigateToTextGraph(navOptions: NavOptions? = null) {
    this.navigate(textGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.textGraph(
    navController: NavController,
) {
    navigation(
        route = textGraphRoutePattern,
        startDestination = textNavigationRoute,
    ) {
        composable(
            route = textNavigationRoute,
        ) {
            TextRoute()
        }
    }
}

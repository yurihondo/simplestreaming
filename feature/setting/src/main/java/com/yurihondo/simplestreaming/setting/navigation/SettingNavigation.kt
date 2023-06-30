package com.yurihondo.simplestreaming.setting.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.yurihondo.simplestreaming.setting.SettingsRoute

const val settingNavigationRoute = "setting_route"
const val settingGraphRoutePattern = "setting_graph"

fun NavController.navigateToSettingGraph(navOptions: NavOptions? = null) {
    this.navigate(settingGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.settingGraph(
    navController: NavController,
) {
    navigation(
        route = settingGraphRoutePattern,
        startDestination = settingNavigationRoute,
    ) {
        settingsRoute()
    }
}

internal fun NavGraphBuilder.settingsRoute() {
    composable(route = settingNavigationRoute) {
        SettingsRoute(
            viewModel = hiltViewModel()
        )
    }
}
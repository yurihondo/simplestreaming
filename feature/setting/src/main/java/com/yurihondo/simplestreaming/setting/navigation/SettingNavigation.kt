package com.yurihondo.simplestreaming.setting.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.activity
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.yurihondo.simplestreaming.setting.SettingsRoute

// setting home
const val settingNavigationRoute = "setting_route"
const val settingGraphRoutePattern = "setting_graph"

// license
const val licenseNavigationRoute = "license_route"

fun NavController.navigateToSettingGraph(navOptions: NavOptions? = null) {
    this.navigate(settingGraphRoutePattern, navOptions)
}

fun NavController.navigateToLicenseActivityRoute(navOptions: NavOptions? = null) {
    this.navigate(licenseNavigationRoute, navOptions)
}

fun NavGraphBuilder.settingGraph(
    navController: NavController,
) {
    navigation(
        route = settingGraphRoutePattern,
        startDestination = settingNavigationRoute,
    ) {
        settingsRoute(onClickLicenseItem = navController::navigateToLicenseActivityRoute)
        licenseActivityRoute()
    }
}

internal fun NavGraphBuilder.settingsRoute(
    onClickLicenseItem: () -> Unit,
) {
    composable(route = settingNavigationRoute) {
        SettingsRoute(
            viewModel = hiltViewModel(),
            onClickLicenseItem = onClickLicenseItem,
        )
    }
}

internal fun NavGraphBuilder.licenseActivityRoute() {
    activity(route = licenseNavigationRoute) {
        activityClass = OssLicensesMenuActivity::class
    }
}

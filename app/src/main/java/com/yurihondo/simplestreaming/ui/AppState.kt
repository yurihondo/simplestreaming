package com.yurihondo.simplestreaming.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.yurihondo.simplestreaming.core.ui.bottomnavigation.BottomNavigationManager
import com.yurihondo.simplestreaming.navigation.TopLevelDestination
import com.yurihondo.simplestreaming.setting.navigation.navigateToSettingGraph
import com.yurihondo.simplestreaming.text.navigation.navigateToTextGraph

class AppState(
    val navHostController: NavHostController,
) : BottomNavigationManager {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    var currentTopLevelDestination by mutableStateOf(TopLevelDestination.TEXT)
        private set

    var shouldShowBottomNavBar by mutableStateOf(true)
        private set

    init {
        navHostController.addOnDestinationChangedListener { _, dest, _ ->
            Log.d("AppState", "destination changed ${dest.route}")
            shouldShowBottomNavBar = true
        }
    }

    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        navOptions {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }.let(
            with(navHostController) {
                return@with when (destination) {
                    TopLevelDestination.TEXT -> ::navigateToTextGraph
                    TopLevelDestination.SETTINGS -> ::navigateToSettingGraph
                }
            }
        )
        currentTopLevelDestination = destination
    }

    override fun hideBottomSheet() {
        Log.d("AppState", "hideBottomSheet")
        shouldShowBottomNavBar = false
    }
}

@Composable
fun rememberAppState(
    navHostController: NavHostController = rememberNavController(),
): AppState {
    return remember(navHostController) { AppState(navHostController) }
}
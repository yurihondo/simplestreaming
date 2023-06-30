package com.yurihondo.simplestreaming.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.yurihondo.simplestreaming.setting.navigation.settingGraph
import com.yurihondo.simplestreaming.text.navigation.textGraph
import com.yurihondo.simplestreaming.text.navigation.textGraphRoutePattern

@Composable
fun MainNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navHostController,
        startDestination = textGraphRoutePattern,
        modifier = modifier,
    ) {
        textGraph(navController = navHostController)
        settingGraph(navController = navHostController)
    }
}
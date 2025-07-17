package com.example.testtaskworkmate

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.testtaskworkmate.ui.screens.details.DetailsScreen
import com.example.testtaskworkmate.ui.screens.home.HomeScreenNew
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class Details(val id: Int)

@Composable
fun RamNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home,
    ) {
        composable<Home> {
            HomeScreenNew(

                onCharacterClick = { id -> navController.navigate(Details(id)) },
            )
        }
        composable<Details> { backStackEntry ->
            val details: Details = backStackEntry.toRoute()
            DetailsScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}

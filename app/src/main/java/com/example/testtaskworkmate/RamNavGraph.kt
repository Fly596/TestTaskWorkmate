package com.example.testtaskworkmate

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testtaskworkmate.ui.screens.details.DetailsScreen
import com.example.testtaskworkmate.ui.screens.home.HomeScreen
import kotlinx.serialization.Serializable

// Объект-дестинация для главного экрана, используется для type-safe навигации..
@Serializable
object Home

// Класс-дестинация для экрана деталей, который принимает ID персонажа в качестве аргумента..
@Serializable
data class Details(val id: Int)

@Composable
// Основной компонент, отвечающий за навигацию в приложении (NavGraph)..
fun RamNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    // NavHost - это контейнер, который отображает текущий экран в зависимости от маршрута..
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home, // Указываем стартовый экран..
    ) {
        // Определяем экран для маршрута Home..
        composable<Home> {
            HomeScreen(
                // Обработчик клика по персонажу, который выполняет навигацию на экран деталей..
                onCharacterClick = { id -> navController.navigate(Details(id)) }
            )
        }
        // Определяем экран для маршрута Details..
        composable<Details> { backStackEntry ->
            DetailsScreen(
                // Обработчик клика для возврата на предыдущий экран..
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
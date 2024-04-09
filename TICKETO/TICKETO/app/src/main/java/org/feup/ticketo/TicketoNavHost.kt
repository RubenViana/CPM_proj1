package org.feup.ticketo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MobileFriendly
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.feup.ticketo.ui.EventTicketViewModel
import org.feup.ticketo.ui.HomeScreen
import org.feup.ticketo.ui.OrdersScreen
import org.feup.ticketo.ui.RegisterScreen
import org.feup.ticketo.ui.SettingsScreen
import org.feup.ticketo.ui.TicketScreen
import org.feup.ticketo.ui.TicketsScreen
import org.feup.ticketo.ui.theme.SetSystemBarsColors
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary


sealed class NavRoutes(val route: String, val icon: ImageVector) {
    data object Home : NavRoutes("home", Icons.Default.Search)
    data object Tickets : NavRoutes("tickets", Icons.Default.MobileFriendly)
    data object Orders : NavRoutes("orders", Icons.Default.AccessTime)
}

@Composable
fun TicketoNavHost(
    navController: NavHostController,
    startDestination: String = "register"
) {
    NavHost(
        navController = navController,
        startDestination
    ) {
        composable(route = "register") {
            RegisterScreen(navController)
        }
        composable(route = NavRoutes.Home.route) {
            SetSystemBarsColors(
                md_theme_light_primary.toArgb(),
                md_theme_light_onPrimary.toArgb(),
                statusTheme = false,
                navigationTheme = true
            )
            HomeScreen(navController)
        }
        composable(route = NavRoutes.Tickets.route) {
            TicketsScreen(navController)
        }
        composable(route = NavRoutes.Orders.route) {
            OrdersScreen(navController)
        }
        composable(route = "settings",
            enterTransition = { return@composable slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) },
            exitTransition = { return@composable slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) },
            popEnterTransition = {
                return@composable slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up
                )
            },
            popExitTransition = {
                return@composable slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down
                )
            }
        ) {
            SettingsScreen(navController)
        }
        composable(route = "tickets/{eventId}") {
            val viewModel =
                EventTicketViewModel(it.arguments?.getInt("eventId") ?: 0, LocalContext.current)
            TicketScreen(navController, viewModel.getEventTickets())
        }
    }
}
package org.feup.ticketo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MobileFriendly
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.feup.ticketo.data.storage.TicketoDatabase
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.ui.screens.eventDetails.EventDetailsScreen
import org.feup.ticketo.ui.screens.eventDetails.EventDetailsViewModel
import org.feup.ticketo.ui.screens.eventTickets.EventTicketsScreen
import org.feup.ticketo.ui.screens.eventTickets.EventTicketsViewModel
import org.feup.ticketo.ui.screens.home.HomeScreen
import org.feup.ticketo.ui.screens.home.HomeViewModel
import org.feup.ticketo.ui.screens.orders.OrdersScreen
import org.feup.ticketo.ui.screens.orders.OrdersViewModel
import org.feup.ticketo.ui.screens.register.RegisterScreen
import org.feup.ticketo.ui.screens.register.RegisterViewModel
import org.feup.ticketo.ui.screens.settings.SettingsScreen
import org.feup.ticketo.ui.screens.settings.SettingsViewModel
import org.feup.ticketo.ui.screens.tickets.TicketsScreen
import org.feup.ticketo.ui.screens.tickets.TicketsViewModel
import org.feup.ticketo.ui.theme.SetSystemBarsColors
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary


sealed class NavRoutes(val route: String, val icon: ImageVector?) {
    data object Home : NavRoutes("home", Icons.Default.Search)
    data object Tickets : NavRoutes("tickets", Icons.Default.MobileFriendly)
    data object Orders : NavRoutes("orders", Icons.Default.AccessTime)
}

@Composable
fun TicketoNavHost(
    navController: NavHostController,
    startDestination: String,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val ticketoDatabase = TicketoDatabase.getDatabase(context)
    val ticketoStorage: TicketoStorage by lazy {
        TicketoStorage(ticketoDatabase.ticketDao())
    }


    NavHost(
        navController = navController,
        startDestination,
    ) {
        composable(route = "register") {
            val viewModel = remember { RegisterViewModel(context, ticketoStorage) }
            RegisterScreen(navController, viewModel, snackbarHostState)
        }
        composable(route = NavRoutes.Home.route) {
            SetSystemBarsColors(
                md_theme_light_primary.toArgb(),
                md_theme_light_onPrimary.toArgb(),
                statusTheme = false,
                navigationTheme = true
            )
            val viewModel = remember { HomeViewModel(context)}
            HomeScreen(navController, context, viewModel)
        }
        composable(route = NavRoutes.Tickets.route) {
            val viewModel = remember { TicketsViewModel() }
            TicketsScreen(navController, viewModel)
        }
        composable(route = NavRoutes.Orders.route) {
            val viewModel = OrdersViewModel()
            OrdersScreen(navController, viewModel)
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
            val viewModel = SettingsViewModel()
            SettingsScreen(navController, viewModel)
        }
        composable(
            route = "tickets/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) {
            val viewModel =
                remember {
                    EventTicketsViewModel(
                        it.arguments?.getInt("eventId") ?: 0,
                        context,
                        ticketoStorage
                    )
                }
            if (viewModel.getEventTickets() != null && viewModel.getEventTickets()?.tickets != null) {
                EventTicketsScreen(navController, viewModel.getEventTickets()!!)
            } else {
                navController.navigate(NavRoutes.Tickets.route)
            }
        }
        composable(
            route = "event/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) {
            val viewModel = EventDetailsViewModel(it.arguments?.getInt("eventId") ?: 0)
            EventDetailsScreen(navController, viewModel)
        }
    }
}
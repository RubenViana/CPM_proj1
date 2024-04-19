package org.feup.ticketo

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookOnline
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.feup.ticketo.data.storage.TicketoDatabase
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.ui.screens.addOrder.AddOrderScreen
import org.feup.ticketo.ui.screens.addOrder.AddOrderViewModel
import org.feup.ticketo.ui.screens.eventDetails.EventDetailsScreen
import org.feup.ticketo.ui.screens.eventDetails.EventDetailsViewModel
import org.feup.ticketo.ui.screens.eventTickets.EventTicketsScreen
import org.feup.ticketo.ui.screens.eventTickets.EventTicketsViewModel
import org.feup.ticketo.ui.screens.home.HomeScreen
import org.feup.ticketo.ui.screens.home.HomeViewModel
import org.feup.ticketo.ui.screens.orderDetails.OrderDetailsScreen
import org.feup.ticketo.ui.screens.orderDetails.OrderDetailsViewModel
import org.feup.ticketo.ui.screens.orders.OrdersScreen
import org.feup.ticketo.ui.screens.orders.OrdersViewModel
import org.feup.ticketo.ui.screens.register.RegisterScreen
import org.feup.ticketo.ui.screens.register.RegisterViewModel
import org.feup.ticketo.ui.screens.settings.SettingsScreen
import org.feup.ticketo.ui.screens.settings.SettingsViewModel
import org.feup.ticketo.ui.screens.tickets.TicketsScreen
import org.feup.ticketo.ui.screens.tickets.TicketsViewModel


sealed class NavRoutes(val route: String, val icon: ImageVector?) {
    data object Home : NavRoutes("home", Icons.Default.Search)
    data object Register : NavRoutes("register", null)
    data object Settings : NavRoutes("settings", null)
    data object EventDetails : NavRoutes("event/{eventId}", null)
    data object Tickets : NavRoutes("tickets", Icons.Outlined.BookOnline)
    data object EventTickets : NavRoutes("tickets/{eventId}", null)
    data object Orders : NavRoutes("orders", Icons.Default.AccessTime)
    data object OrderDetails : NavRoutes("orders/{orderId}", Icons.Default.AccessTime)
    data object AddOrder : NavRoutes("addOrder", null)
}

@Composable
fun TicketoNavHost(
    modifier: Modifier = Modifier,
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
        composable(route = NavRoutes.Register.route) {
            val viewModel = remember { RegisterViewModel(context, ticketoStorage) }
            RegisterScreen(navController, viewModel, snackbarHostState)
        }
        composable(route = NavRoutes.Home.route) {
            val viewModel = remember { HomeViewModel(context, ticketoStorage) }
            HomeScreen(navController, context, viewModel, modifier)
        }
        composable(route = NavRoutes.Tickets.route) {
            val viewModel = remember { TicketsViewModel(context, ticketoStorage) }
            TicketsScreen(navController, viewModel, modifier)
        }
        composable(route = NavRoutes.Orders.route) {
            val viewModel = remember { OrdersViewModel(context, ticketoStorage) }
            OrdersScreen(navController, viewModel, modifier)
        }
        composable(route = NavRoutes.AddOrder.route) {
            val viewModel = AddOrderViewModel(context, ticketoStorage)
            AddOrderScreen(navController, viewModel)
        }
        composable(route = NavRoutes.Settings.route,
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
            route = NavRoutes.EventTickets.route,
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
            EventTicketsScreen(navController, viewModel)
        }
        composable(
            route = NavRoutes.EventDetails.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) {
            val viewModel = remember {
                EventDetailsViewModel(
                    it.arguments?.getInt("eventId") ?: 0,
                    context,
                    ticketoStorage
                )
            }
            EventDetailsScreen(navController, viewModel)
        }
        composable(
            route = NavRoutes.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) {
            val viewModel = remember {
                OrderDetailsViewModel(
                    it.arguments?.getInt("orderId") ?: 0,
                    context,
                    ticketoStorage
                )
            }
            OrderDetailsScreen(navController, viewModel)
        }

    }
}



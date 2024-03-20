package org.feup.ticketo

import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MobileFriendly
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.feup.ticketo.ui.HomeScreen
import org.feup.ticketo.ui.OrdersScreen
import org.feup.ticketo.ui.TicketsScreen
import org.feup.ticketo.ui.SettingsScreen


sealed class NavRoutes (val route: String, val icon: ImageVector) {
    data object Home : NavRoutes("home", Icons.Default.Home)
    data object Tickets : NavRoutes("tickets", Icons.Default.MobileFriendly)
    data object Orders : NavRoutes("orders", Icons.Default.AccessTime)
    data object Settings : NavRoutes("settings", Icons.Default.Settings)
}

@Composable
fun TicketoNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        composable(route = NavRoutes.Home.route){
            HomeScreen()
        }
        composable(route = NavRoutes.Tickets.route){
            TicketsScreen()
        }
        composable(route = NavRoutes.Orders.route){
            OrdersScreen()
        }
        composable(route = NavRoutes.Settings.route){
            SettingsScreen()
        }
    }
}
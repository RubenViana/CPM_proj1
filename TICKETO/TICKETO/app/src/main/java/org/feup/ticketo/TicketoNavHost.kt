package org.feup.ticketo

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.feup.ticketo.ui.HomeScreen
import org.feup.ticketo.ui.OrdersScreen
import org.feup.ticketo.ui.TicketsScreen
import org.feup.ticketo.ui.SettingsScreen

@Composable
fun TicketoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(route = Home.route){
            HomeScreen()
        }
        composable(route = Tickets.route){
            TicketsScreen()
        }
        composable(route = Orders.route){
            OrdersScreen()
        }
        composable(route = "Settings"){
            SettingsScreen()
        }
    }
}
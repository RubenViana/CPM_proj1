package org.feup.ticketo.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.feup.ticketo.NavRoutes
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val navOptions = listOf(NavRoutes.Home, NavRoutes.Tickets, NavRoutes.Orders)
    val opts = remember { navOptions.map { it.route } }

    if (currentRoute in opts) {
        NavigationBar(
            containerColor = md_theme_light_onPrimary
        ) {
            navOptions.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo("home")
                            launchSingleTop = true
                        }
                    },
                    icon = { item.icon?.let { Icon(imageVector = it, contentDescription = null) } },
                    label = { Text(text = item.route.replaceFirstChar { item.route.take(1).uppercase() }) } // This is for making the 1st letter uppercase
                )
            }
        }
    }
}
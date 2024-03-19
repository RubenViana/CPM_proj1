package org.feup.ticketo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.feup.ticketo.ui.HomeScreen
import org.feup.ticketo.ui.OrdersScreen
import org.feup.ticketo.ui.TicketsScreen

interface TicketoDestination {
    val icon: ImageVector
    val route: String
}

object Home : TicketoDestination {
    override val icon = Icons.Default.Home
    override val route = "Home"
}

object Tickets : TicketoDestination {
    override val icon = Icons.Default.MailOutline
    override val route = "Tickets"
}

object Orders : TicketoDestination {
    override val icon = Icons.Default.ShoppingCart
    override val route = "Orders"
}

// NavBar items
val bottomNavBarOpts = listOf(Home, Tickets, Orders)
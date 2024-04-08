package org.feup.ticketo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.feup.ticketo.data.Ticket
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary

@Composable
fun TicketsScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize().padding(10.dp),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(10) {
                TicketCard(Ticket(ticket_id = "1", purchase_id = 1, event_id = 1, purchase_date = "2022-01-01", used = false, qrcode = "qrcode", place = "A1"), navController)
            }
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket, navController: NavHostController) {
    ElevatedCard(
        modifier = Modifier.padding(10.dp),
        onClick = { navController.navigate("ticket/${ticket.ticket_id}") }
    ) {
        ListItem(
            headlineContent = { Text(text = "Ticket #${ticket.ticket_id}") },
            supportingContent = { Text(text = "Date: {} | Seat: ${ticket.place}") },
            leadingContent = {
                Image(painter = ColorPainter(Color.Gray), contentDescription = null, modifier = Modifier.size(56.dp))
            }
        )
    }
}
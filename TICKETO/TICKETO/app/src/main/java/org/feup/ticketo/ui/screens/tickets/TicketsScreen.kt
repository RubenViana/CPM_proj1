package org.feup.ticketo.ui.screens.tickets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PanoramaVertical
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun TicketsScreen(
    navController: NavHostController,
    viewModel: TicketsViewModel
) {


    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(viewModel.ticketsByEventList.size) {
                item -> TicketCard(ticket = viewModel.ticketsByEventList[item], navController = navController)
            }
        }
    }
}

@Composable
fun TicketCard(ticket: TicketsByEvent, navController: NavHostController) {
    ElevatedCard(
        modifier = Modifier.padding(10.dp),
        onClick = { navController.navigate("tickets/${ticket.eventId}") }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = ColorPainter(Color.Gray), contentDescription = null, modifier = Modifier.size(50.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp)
            ) {
                Text(ticket.eventName, style = MaterialTheme.typography.bodyLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(ticket.eventDate, style = MaterialTheme.typography.bodyMedium)
            }
            Column(
            ) {
                Row {
                    Text(text = "${ticket.numberTickets}x", style = MaterialTheme.typography.bodySmall)
                    Icon(imageVector = Icons.Default.PanoramaVertical, contentDescription = null)
                }
            }
        }
    }
}
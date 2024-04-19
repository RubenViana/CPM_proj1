package org.feup.ticketo.ui.screens.tickets

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.PanoramaVertical
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.EventWithTicketsCount
import org.feup.ticketo.ui.components.serverErrorToast
import org.feup.ticketo.ui.theme.md_theme_light_primary

@Composable
fun TicketsScreen(navController: NavHostController, viewModel: TicketsViewModel, modifier: Modifier) {
    // Fetch tickets from server whenever the screen is launched
    LaunchedEffect(viewModel) {
        viewModel.fetchTickets()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (viewModel.serverValidationState.value) {
            is ServerValidationState.Loading -> {
                LoadingTickets(
                    (viewModel.serverValidationState.value as ServerValidationState.Loading).message
                        ?: "Loading tickets..."
                )
            }

            is ServerValidationState.Failure -> {
                EmptyList()
            }


            is ServerValidationState.Success -> {
                if (viewModel.eventsWithTicketsCount.value.isEmpty()) {
                    EmptyList()
                } else {
                    TicketsList(viewModel.eventsWithTicketsCount, navController)
                }
            }
        }

        when {
            viewModel.showServerErrorToast.value -> {
                if (viewModel.serverValidationState.value is ServerValidationState.Failure) {
                    serverErrorToast(
                        "Error getting tickets from server",
                        (viewModel.serverValidationState.value as ServerValidationState.Failure).error
                    )
                    viewModel.showServerErrorToast.value = false
                }
            }
        }
    }
}

@Composable
fun TicketsList(tickets: MutableState<List<EventWithTicketsCount>>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(tickets.value.size) { index ->
            TicketCard(eventWithTickets = tickets.value[index], navController = navController)
        }
    }
}

@Composable
fun TicketCard(eventWithTickets: EventWithTicketsCount, navController: NavHostController) {
    ElevatedCard(
        modifier = Modifier.padding(10.dp),
        onClick = { navController.navigate("tickets/${eventWithTickets.event.event_id}") }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            eventWithTickets.event.picture?.let {
                BitmapFactory.decodeByteArray(eventWithTickets.event.picture, 0, it.size).asImageBitmap()
            }
                ?.let {
                    Image(
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp),
                        bitmap = it
                    )
                }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp)
            ) {
                eventWithTickets.event.name?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                eventWithTickets.event.date?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }

            Row (
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "${eventWithTickets.tickets_count}x",
                    style = MaterialTheme.typography.bodySmall
                )
                Icon(imageVector = Icons.Outlined.ConfirmationNumber, contentDescription = null)
            }

        }
    }
}

@Composable
fun LoadingTickets(message: String) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = Color.Blue
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = message,
            style = TextStyle(
                color = md_theme_light_primary,
                fontSize = 22.sp
            )
        )
    }
}

@Composable
fun EmptyList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No tickets available!",
            style = TextStyle(
                color = md_theme_light_primary,
                fontSize = 22.sp
            )
        )
    }
}
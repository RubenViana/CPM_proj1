package org.feup.ticketo.ui.screens.home

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.ui.components.serverErrorToast
import org.feup.ticketo.ui.theme.md_theme_light_primary

@Composable
fun HomeScreen(navController: NavHostController, context: Context, viewModel: HomeViewModel) {
    // Fetch events from server whenever the screen is launched
    LaunchedEffect(viewModel) {
        viewModel.fetchEvents()
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        when (viewModel.serverValidationState.value) {
            is ServerValidationState.Loading -> {
                LoadingEvents((viewModel.serverValidationState.value as ServerValidationState.Loading)?.message ?: "Loading events...")
            }

            is ServerValidationState.Failure -> {
                EmptyList()
            }

            is ServerValidationState.Success -> {
                if (viewModel.events.value.isEmpty()) {
                    EmptyList()
                } else {
                    EventList(events = viewModel.events.value, navController = navController)
                }
            }
        }

        when {
            viewModel.showServerErrorToast.value -> {
                if (viewModel.serverValidationState.value is ServerValidationState.Failure) {
                    serverErrorToast(
                        "Error getting events from server",
                        (viewModel.serverValidationState.value as ServerValidationState.Failure).error
                    )
                    viewModel.showServerErrorToast.value = false
                }
            }
        }

    }
}

@Composable
fun LoadingEvents(s: String) {
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
            text = s,
            style = TextStyle(
                color = md_theme_light_primary,
                fontSize = 22.sp
            )
        )
    }
}

@Composable
private fun EmptyList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No events available!",
            style = TextStyle(
                color = md_theme_light_primary,
                fontSize = 22.sp
            )
        )
    }
}

@Composable
fun EventList(events: List<Event>, navController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(events.size) { item ->
            EventCard(events[item], navController)
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    navController: NavHostController
) {
    OutlinedCard(
        onClick = { navController.navigate("event/${event.event_id}") },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        modifier = Modifier
            .padding(10.dp)
            .width(180.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            event.picture?.let { BitmapFactory.decodeByteArray(event.picture, 0, it.size).asImageBitmap() }
                ?.let {
                    Image(
//                        painter = ColorPainter(Color.Gray),
                        contentDescription = null,
                        modifier = Modifier
                            .size(180.dp)
                            .fillMaxWidth(),
                        bitmap = it
                    )
                }
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.name.toString(),
                        style = TextStyle(
                            color = md_theme_light_primary,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = event.price.toString() + "€",
                        style = TextStyle(
                            color = md_theme_light_primary,
                            fontSize = 16.sp
                        )
                    )
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier.background(
                        color = md_theme_light_primary,
                        shape = RoundedCornerShape(10.dp)
                    )
                ) {
                    Icon(Icons.Default.Add, tint = Color.White, contentDescription = null)
                }
            }
        }
    }
}
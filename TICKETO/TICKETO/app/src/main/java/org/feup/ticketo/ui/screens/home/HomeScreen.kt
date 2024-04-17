package org.feup.ticketo.ui.screens.home

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.ui.components.serverErrorToast
import org.feup.ticketo.ui.theme.md_theme_light_primary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(navController: NavHostController, context: Context, viewModel: HomeViewModel, modifier: Modifier) {
    // Fetch events from server whenever the screen is launched
    LaunchedEffect(viewModel) {
        viewModel.fetchEvents()
    }

    Surface(
        modifier = modifier.fillMaxSize()
    ) {

        when (viewModel.serverValidationState.value) {
            is ServerValidationState.Loading -> {
                LoadingEvents((viewModel.serverValidationState.value as ServerValidationState.Loading)?.message ?: "Loading Events")
            }

            is ServerValidationState.Failure -> {
                EmptyList()
            }

            is ServerValidationState.Success -> {
                EventList(events = viewModel.events.value, navController = navController)
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
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = md_theme_light_primary
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
    ElevatedCard(
        onClick = { navController.navigate("event/${event.event_id}") },
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(10.dp)
    ) {
        Box(modifier = Modifier.height(250.dp)){
            event.picture?.let { BitmapFactory.decodeByteArray(event.picture, 0, it.size).asImageBitmap() }
                ?.let {
                    Image(
                        contentDescription = "Event picture",
                        bitmap = it,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        ),
                        startY = 300f,
                    )
                )
            )
            Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.TopEnd){
                Text(text = formatDate(event.date!!), style = TextStyle(color = Color.White, fontSize = 16.sp), modifier = Modifier.background(Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)))
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(12.dp), contentAlignment = Alignment.BottomStart){
                Text(text = event.name!!, style = TextStyle(color = Color.White, fontSize = 16.sp))
            }
        }
    }
}

fun formatDate(input: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    val dateTime = LocalDateTime.parse(input, formatter)
    val dayOfMonth = dateTime.dayOfMonth
    val month = dateTime.month.toString().substring(0, 3)
    return "$dayOfMonth $month"
}
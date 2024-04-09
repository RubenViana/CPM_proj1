package org.feup.ticketo.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.ui.theme.md_theme_light_primary
import org.feup.ticketo.utils.serverUrl

@Composable
fun HomeScreen(navController: NavHostController) {
    val event = Event(
        event_id = 1,
        name = "Event 1",
        date = "2022-01-01",
        price = 10.0f,
        picture = ByteArray(0)
    )

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            items(10) {
                EventCard(event)
            }
        }
    }


}

@Composable
fun EventCard(
    event: Event
) {
    OutlinedCard(
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
            Image(
                painter = ColorPainter(Color.Gray),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .fillMaxWidth()
            )
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
                    Icon(Icons.Default.Add, tint = Color.White,  contentDescription = null)
                }
            }
        }
    }
}


fun getNextEventsFromServer(context: Context, nr_of_events: Int) {

    // Server endpoint
    val endpoint = "/next_events?nr_of_events=$nr_of_events"

    // Create the request
    val request = JsonObjectRequest(
        Request.Method.POST, serverUrl + endpoint, null,
        { response ->
            // Handle response
        },
        { error ->
            //
        }
    )

    // Add the request to the RequestQueue
    Volley.newRequestQueue(context).add(request)

}
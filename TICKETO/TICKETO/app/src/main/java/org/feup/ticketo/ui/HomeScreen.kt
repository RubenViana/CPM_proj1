package org.feup.ticketo.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.feup.ticketo.utils.serverUrl

@Composable
fun HomeScreen() {

    Column(
        Modifier.fillMaxSize()
    ) {
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
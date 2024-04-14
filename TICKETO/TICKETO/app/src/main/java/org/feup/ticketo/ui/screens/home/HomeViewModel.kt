package org.feup.ticketo.ui.screens.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.utils.serverUrl

class HomeViewModel(private val context: Context) : ViewModel() {
    val serverValidationState = mutableStateOf<ServerValidationState?>(null)
    val showServerErrorToast = mutableStateOf(false)

    val events = mutableStateOf<List<Event>>(emptyList())

    fun fetchEvents() {
        serverValidationState.value = ServerValidationState.Loading("Loading events...")
        getNextEventsFromServer()
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getNextEventsFromServer(nr_of_events: Int = 10) {
        val url = serverUrl + "next_events?nr_of_events=$nr_of_events"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val eventsList = mutableListOf<Event>()
                for (i in 0 until response.getJSONArray("events").length()) {
                    val event = response.getJSONArray("events").getJSONObject(i)
                    eventsList.add(
                        Event(
                            event_id = event.getInt("EVENT_ID"),
                            name = event.getString("NAME"),
                            date = event.getString("DATE"),
                            price = event.getDouble("PRICE").toFloat(),
                            picture = event.getString("PICTURE").hexToByteArray()
                        )
                    )
                }
                events.value = eventsList
                serverValidationState.value = ServerValidationState.Success(response)
            },
            { error ->
                serverValidationState.value = ServerValidationState.Failure(error)
                showServerErrorToast.value = true
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

}
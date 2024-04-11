package org.feup.ticketo.ui.screens.home

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.utils.serverUrl

class HomeViewModel(private val context: Context) : ViewModel() {
    private val serverValidationState = mutableStateOf<ServerValidationState?>(null)
    private val showServerErrorToast = mutableStateOf(false)

    val events = emptyList<Event>()

    fun fetchEvents(): List<Event> {
        try {
            getNextEventsFromServer()
            return when (val state = serverValidationState.value) {
                is ServerValidationState.Success -> {
                    val events = state.response.getJSONArray("events")
                    val eventsList = mutableListOf<Event>()
                    for (i in 0 until events.length()) {
                        val event = events.getJSONObject(i)
                        eventsList.add(
                            Event(
                                event_id = event.getInt("event_id"),
                                name = event.getString("name"),
                                date = event.getString("date"),
                                price = event.getDouble("price").toFloat(),
                                picture = ByteArray(0)
                            )
                        )
                    }
                    eventsList
                }

                else -> {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            return emptyList()
        }
    }

    private fun getNextEventsFromServer(nr_of_events: Int = 10) {
        val url = serverUrl + "next_events?nr_of_events=$nr_of_events"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
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
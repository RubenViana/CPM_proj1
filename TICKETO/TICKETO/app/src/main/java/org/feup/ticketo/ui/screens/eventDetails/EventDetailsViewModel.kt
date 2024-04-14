package org.feup.ticketo.ui.screens.eventDetails

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.serverMessages.ticketPurchaseMessage
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences
import org.feup.ticketo.utils.objectToJson
import org.feup.ticketo.utils.serverUrl

class EventDetailsViewModel(
    private val eventId: Int,
    private val context: Context
) : ViewModel() {

    val fetchEventFromServerState = mutableStateOf<ServerValidationState>(ServerValidationState.Loading("Loading event details..."))
    val purchaseTicketsInServerState = mutableStateOf<ServerValidationState?>(null)

    var event by mutableStateOf(Event(-1, "", "", ByteArray(0), 0.0f))

    var numberTickets by mutableIntStateOf(0)

    @OptIn(ExperimentalStdlibApi::class)
    fun fetchEvent() {
        // get event from server
        val url = serverUrl + "event?event_id=$eventId"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                event = Event(
                    event_id = response.getJSONObject("event").getInt("EVENT_ID"),
                    name = response.getJSONObject("event").getString("NAME"),
                    date = response.getJSONObject("event").getString("DATE"),
                    price = response.getJSONObject("event").getDouble("PRICE").toFloat(),
                    picture = response.getJSONObject("event").getString("PICTURE").hexToByteArray()
                )
                fetchEventFromServerState.value = ServerValidationState.Success(response)
            },
            { error ->
                fetchEventFromServerState.value = ServerValidationState.Failure(error)
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

    fun increaseTickets() {
        if (numberTickets < 4) {
            numberTickets++
        }
    }

    fun decreaseTickets() {
        if (numberTickets > 0) {
            numberTickets--
        }
    }

    fun checkout() {
        purchaseTicketsInServerState.value = ServerValidationState.Loading("Purchasing tickets...")
        // Send order to server
        val endpoint = "buy_ticket"
        // Create the request body
        val json = objectToJson(
            ticketPurchaseMessage(
                getUserIdInSharedPreferences(context),
                eventId,
                numberTickets,
            )
        )

        // Create the request
        val request = JsonObjectRequest(
            Request.Method.POST, serverUrl + endpoint, json,
            { response ->
                purchaseTicketsInServerState.value = ServerValidationState.Success(response)
            },
            { error ->
                purchaseTicketsInServerState.value = ServerValidationState.Failure(error)
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(request)

    }
}
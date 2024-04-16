package org.feup.ticketo.ui.screens.tickets

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences
import org.feup.ticketo.utils.serverUrl
import java.text.SimpleDateFormat
import java.util.Locale

class TicketsViewModel(private val context: Context) : ViewModel() {
    val serverValidationState = mutableStateOf<ServerValidationState?>(null)
    val showServerErrorToast = mutableStateOf(false)

    val ticketsByEventList = mutableStateOf<List<TicketsByEvent>>(emptyList())

    fun fetchTickets() {
        serverValidationState.value = ServerValidationState.Loading("Loading tickets...")
        getTicketsFromServer(getUserIdInSharedPreferences(context))
    }
    private fun getTicketsFromServer(customerId: String) {
        val url = serverUrl + "tickets?customer_id=$customerId"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val ticketsList = mutableListOf<TicketsByEvent>()
                val ticketsArray = response.getJSONArray("tickets")
                for (i in 0 until ticketsArray.length()) {
                    val ticket = ticketsArray.getJSONObject(i)
                    val eventDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(ticket.getString("DATE"))
                    val formattedEventDate = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(eventDate)
                    val ticketsByEvent = TicketsByEvent(
                        eventId = ticket.getInt("EVENT_ID"),
                        eventName = ticket.getString("NAME"),
                        numberTickets = ticket.getInt("nr_of_tickets"),
                        eventDate = formattedEventDate
                    )
                    ticketsList.add(ticketsByEvent)
                }
                ticketsByEventList.value = ticketsList
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

//example data class -> Move out of here
data class TicketsByEvent(
    val eventId: Int,
    val eventName: String,
    val numberTickets: Int,
    val eventDate: String
)
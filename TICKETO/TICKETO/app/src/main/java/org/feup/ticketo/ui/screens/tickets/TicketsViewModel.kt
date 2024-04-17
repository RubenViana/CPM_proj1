package org.feup.ticketo.ui.screens.tickets

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.EventWithTicketsCount
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences

class TicketsViewModel(private val context: Context, private val ticketoStorage: TicketoStorage) :
    ViewModel() {
    val serverValidationState = mutableStateOf<ServerValidationState?>(null)
    val showServerErrorToast = mutableStateOf(false)

    val eventsWithTicketsCount = mutableStateOf<List<EventWithTicketsCount>>(emptyList())

    fun fetchTickets() {
        serverValidationState.value = ServerValidationState.Loading("Loading tickets...")
//        getTicketsFromServer(getUserIdInSharedPreferences(context))
        getTicketsFromLocalStorage()
    }

    private fun getTicketsFromLocalStorage() {
        viewModelScope.launch {
            ticketoStorage.getEventsWithTicketCount(getUserIdInSharedPreferences(context))?.let {
                eventsWithTicketsCount.value = it
                serverValidationState.value = ServerValidationState.Success(null)
            }
        }
    }

//    @OptIn(ExperimentalStdlibApi::class)
//    private fun getTicketsFromServer(customerId: String) {
//        val url = serverUrl + "tickets?customer_id=$customerId"
//        val request = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response ->
//                val ticketsList = mutableListOf<TicketsByEvent>()
//                val ticketsArray = response.getJSONArray("tickets")
//                for (i in 0 until ticketsArray.length()) {
//                    val ticket = ticketsArray.getJSONObject(i)
//                    val eventDate = SimpleDateFormat(
//                        "yyyy-MM-dd HH:mm:ss",
//                        Locale.getDefault()
//                    ).parse(ticket.getString("DATE"))
//                    val formattedEventDate =
//                        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(eventDate)
//                    val ticketsByEvent = TicketsByEvent(
//                        eventId = ticket.getInt("EVENT_ID"),
//                        eventName = ticket.getString("NAME"),
//                        numberTickets = ticket.getInt("nr_of_tickets"),
//                        eventDate = formattedEventDate,
//                        picture = ticket.getString("PICTURE").hexToByteArray()
//                    )
//                    ticketsList.add(ticketsByEvent)
//                }
//                ticketsByEventList.value = ticketsList
//                serverValidationState.value = ServerValidationState.Success(response)
//            },
//            { error ->
//                serverValidationState.value = ServerValidationState.Failure(error)
//                showServerErrorToast.value = true
//            }
//        )

//        Volley.newRequestQueue(context).add(request)
//    }
}


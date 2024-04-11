package org.feup.ticketo.ui.screens.tickets

import androidx.lifecycle.ViewModel

class TicketsViewModel : ViewModel() {

    val ticketsByEventList: List<TicketsByEvent> = getTicketsByEvent()
    fun getTicketsByEvent(): List<TicketsByEvent> {
        // Example:
        return listOf(
            TicketsByEvent(
                1,
                "Event 1",
                2,
                "2024-08-20"
            )
        )
    }
}

//example data class -> Move out of here
data class TicketsByEvent(
    val eventId: Int,
    val eventName: String,
    val numberTickets: Int,
    val eventDate: String
)
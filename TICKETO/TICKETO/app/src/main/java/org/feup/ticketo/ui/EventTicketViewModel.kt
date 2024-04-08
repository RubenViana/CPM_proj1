package org.feup.ticketo.ui

import androidx.lifecycle.ViewModel
import org.feup.ticketo.data.Event
import org.feup.ticketo.data.Ticket

class EventTicketViewModel(
    private val eventId: Int,
) : ViewModel() {

    fun getEventTickets(): EventTickets {
        return EventTickets(
            getEvent().name,
            getEvent().date,
            getEvent().picture,
            getTickets()
        )
    }

    fun getEvent(): Event {
        return Event(
            event_id = eventId,
            name = "Event 1",
            date = "2022-01-01",
            price = 10.0f,
            picture = ByteArray(0)
        )
    }

    fun getTickets(): List<Ticket> {
        return listOf(
            Ticket(
                ticket_id = "1",
                purchase_id = 1,
                event_id = eventId,
                purchase_date = "2022-01-01",
                used = false,
                qrcode = "qrcode",
                place = "A1"
            ),
            Ticket(
                ticket_id = "2",
                purchase_id = 2,
                event_id = eventId,
                purchase_date = "2022-01-02",
                used = false,
                qrcode = "qrcode",
                place = "A2"
            )
        )
    }
}

data class EventTickets(
    val eventName: String,
    val eventDate: String,
    val eventImage: ByteArray,
    val tickets: List<Ticket>
)
package org.feup.ticketo.ui.screens.eventTickets

import android.content.Context
import androidx.lifecycle.ViewModel
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.TicketoDatabase
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences

class EventTicketsViewModel(
    private val eventId: Int,
    private val context: Context
) : ViewModel() {
    private val db = TicketoDatabase.getDatabase(context = context)
    fun getEventTickets(): EventTickets {
        val event = getEvent()
        return EventTickets(
            event?.name,
            event?.date,
            event?.picture,
            getTickets()
        )
    }

    private fun getEvent(): Event? {
        return db.ticketDao().getEvent(eventId)
    }

    private fun getTickets(): List<Ticket>? {
        return db.ticketDao().getCustomerTicketsForEvent(
            eventId = eventId,
            customerId = getUserIdInSharedPreferences(context)
        )
    }
}

data class EventTickets(
    val eventName: String?,
    val eventDate: String?,
    val eventImage: ByteArray?,
    val tickets: List<Ticket>?
)
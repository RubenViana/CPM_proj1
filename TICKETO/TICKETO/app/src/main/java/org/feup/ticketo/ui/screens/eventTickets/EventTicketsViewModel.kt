package org.feup.ticketo.ui.screens.eventTickets

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences

class EventTicketsViewModel(
    private val eventId: Int,
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {

    private var eventTickets: EventTickets? = null

    init {
        viewModelScope.launch {
            val event = getEvent()
            eventTickets = EventTickets(
                event?.name.orEmpty(),
                event?.date.orEmpty(),
                event?.picture?: ByteArray(0),
                getTickets()?: null
            )

        }
    }

    fun getEventTickets(): EventTickets? {
        return eventTickets
    }

    private suspend fun getEvent(): Event? {
        return ticketoStorage.getEvent(eventId)
    }

    private suspend fun getTickets(): List<Ticket>? {
        return ticketoStorage.getCustomerTicketsForEvent(
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
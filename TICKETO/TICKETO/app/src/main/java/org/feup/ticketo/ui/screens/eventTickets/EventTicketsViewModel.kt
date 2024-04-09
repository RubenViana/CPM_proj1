package org.feup.ticketo.ui.screens.eventTickets

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.TicketoDatabase
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences

class EventTicketsViewModel(
    private val eventId: Int,
    private val context: Context
) : ViewModel() {
    private val ticketoDatabase = TicketoDatabase.getDatabase(context = context)

    private val ticketoStorage: TicketoStorage by lazy {
        TicketoStorage(ticketoDatabase.ticketDao())
    }

    private lateinit var eventTickets: EventTickets

    init {
//        Log.i("mytag", eventTickets.toString())
        viewModelScope.launch {
            val event = getEvent()
            eventTickets = EventTickets(
                event?.name.orEmpty(),
                event?.date.orEmpty(),
                event?.picture?: ByteArray(0),
                getTickets()?: null
            )

        }
//        eventTickets = EventTickets(null, null, null, null)
        Log.i("mytag", eventTickets.toString())

    }

    fun getEventTickets(): EventTickets {
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
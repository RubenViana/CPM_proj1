package org.feup.ticketo.ui.screens.eventDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.feup.ticketo.data.storage.Event

class EventDetailsViewModel(
    private val eventId: Int,
) : ViewModel() {
    val event = Event(
        event_id = eventId,
        name = "Event 1",
        date = "2022-01-01",
        price = 10.0f,
        picture = ByteArray(0)
    )
    var numberTickets by mutableIntStateOf(0)

    // retrieve event from database with eventId
    fun getEventById(): Event {
        return event
    }

    fun increaseTickets() {
        numberTickets++
    }

    fun decreaseTickets() {
        if (numberTickets > 0) {
            numberTickets--
        }
    }

    fun checkout() {
        // checkout tickets
        // comunication with server
    }
}
package org.feup.ticketo.ui.screens.eventDetails

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
}
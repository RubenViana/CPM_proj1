package org.feup.ticketo.ui.screens.home

import androidx.lifecycle.ViewModel
import org.feup.ticketo.data.storage.Event

class HomeViewModel(
    //repo = localRepo
    //api = serverAPI
) : ViewModel() {
    val eventsList = getEvents()

    private fun getEvents(): List<Event> {
        return listOf(
            Event(
                event_id = 1,
                name = "Event 1",
                date = "2022-01-01",
                price = 10.0f,
                picture = ByteArray(0)
            )
        )
    }
}
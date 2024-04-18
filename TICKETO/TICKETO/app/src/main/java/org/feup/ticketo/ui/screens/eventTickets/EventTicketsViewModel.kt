package org.feup.ticketo.ui.screens.eventTickets

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.storage.EventTickets
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences

class EventTicketsViewModel(
    private val eventId: Int,
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {

    val eventTickets = mutableStateOf<EventTickets?>(null)
    val selectedTickets = mutableStateOf<List<Ticket>>(emptyList())

    fun getEventTickets() {
        viewModelScope.launch {
            eventTickets.value = ticketoStorage.getCustomerTicketsForEvent(
                eventId = eventId,
                customerId = getUserIdInSharedPreferences(context)
            )
        }
    }

    fun handleTicketOnCheckedChange(ticket: Ticket) {
        if (selectedTickets.value.size < 4 && !selectedTickets.value.contains(ticket)) {
            val temp = selectedTickets.value.toMutableList()
            temp.add(ticket)
            selectedTickets.value = temp
        } else {
            val temp = selectedTickets.value.toMutableList()
            temp.remove(ticket)
            selectedTickets.value = temp
        }
        Log.i("selectedTickets", selectedTickets.value.size.toString())
    }

    fun validateTickets() {
        // validate tickets in server
    }

}

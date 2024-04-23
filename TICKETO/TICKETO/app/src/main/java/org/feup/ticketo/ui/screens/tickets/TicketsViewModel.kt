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
        getTicketsFromLocalStorage()
    }

    private fun getTicketsFromLocalStorage() {
        viewModelScope.launch {
            ticketoStorage.getEventsWithUnusedTicketCount(getUserIdInSharedPreferences(context))?.let {
                eventsWithTicketsCount.value = it
                serverValidationState.value = ServerValidationState.Success(null)
            }
        }
    }
}


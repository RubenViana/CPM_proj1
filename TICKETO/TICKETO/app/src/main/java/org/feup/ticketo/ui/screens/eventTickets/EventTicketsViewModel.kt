package org.feup.ticketo.ui.screens.eventTickets

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.serverMessages.ticketValidationMessage
import org.feup.ticketo.data.storage.Customer
import org.feup.ticketo.data.storage.EventTickets
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences
import org.feup.ticketo.utils.generateQRCode

class EventTicketsViewModel(
    private val eventId: Int,
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {

    val fetchTicketsFromDatabaseState = mutableStateOf<ServerValidationState?>(null)
    val qrCodeGenerationState = mutableStateOf<ServerValidationState?>(null)
    val selectTicketsToQRCodeState = mutableStateOf(false)
    val openQRCodeGenerationConfirmationDialog = mutableStateOf(false)

    val eventTickets = mutableStateOf<EventTickets?>(null)
    val selectedTickets = mutableStateOf<List<Ticket>>(emptyList())
    val qrCode = mutableStateOf<Bitmap?>(null)

    fun getEventTickets() {
        viewModelScope.launch {
            val event = ticketoStorage.getEventById(eventId)
            val tickets = ticketoStorage.getUnusedCustomerTicketsForEvent(
                eventId = eventId,
                customerId = getUserIdInSharedPreferences(context)
            )
            eventTickets.value = EventTickets(event, tickets)
            fetchTicketsFromDatabaseState.value = ServerValidationState.Success(null, "Tickets loaded")
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
    }

    fun validateTickets() {
        qrCodeGenerationState.value = ServerValidationState.Loading("Generation QR Code...")
        // create ticket validation message
        val tvm = ticketValidationMessage(
            Customer(getUserIdInSharedPreferences(context)),
            selectedTickets.value,
            null
        )

        try {
            // generate QR code
            qrCode.value = generateQRCode(tvm)

            if (qrCode.value != null) {
                selectedTickets.value.forEach { ticket ->
                    viewModelScope.launch {
                        ticketoStorage.setTicketAsUsed(ticket.ticket_id)
                    }
                }
                // remove tickets from selectedTickets
                selectedTickets.value = emptyList()
                qrCodeGenerationState.value =
                    ServerValidationState.Success(null, "QR code generated successfully!")
            } else {
                qrCodeGenerationState.value =
                    ServerValidationState.Failure(null, "Error generating QR code")
            }

        } catch (e: Exception) {
            qrCodeGenerationState.value =
                ServerValidationState.Failure(null, "Error generating QR code")
        }

    }

}

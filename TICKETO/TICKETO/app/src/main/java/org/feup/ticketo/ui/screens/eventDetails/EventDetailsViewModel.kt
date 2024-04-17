package org.feup.ticketo.ui.screens.eventDetails

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.serverMessages.ticketPurchaseMessage
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Purchase
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.Voucher
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences
import org.feup.ticketo.utils.formatDate
import org.feup.ticketo.utils.objectToJson
import org.feup.ticketo.utils.serverUrl
import org.json.JSONObject

class EventDetailsViewModel(
    private val eventId: Int,
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {

    val fetchEventFromServerState = mutableStateOf<ServerValidationState>(ServerValidationState.Loading("Loading event details..."))
    val purchaseTicketsInServerState = mutableStateOf<ServerValidationState?>(null)

    var event by mutableStateOf(Event(-1, "", "", ByteArray(0), 0.0f))

    var numberTickets by mutableIntStateOf(0)

    @OptIn(ExperimentalStdlibApi::class)
    fun fetchEvent() {
        // get event from server
        val url = serverUrl + "event?event_id=$eventId"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

                event = Event(
                    event_id = response.getJSONObject("event").getInt("EVENT_ID"),
                    name = response.getJSONObject("event").getString("NAME"),
                    date = formatDate(response.getJSONObject("event").getString("DATE")),
                    price = response.getJSONObject("event").getDouble("PRICE").toFloat(),
                    picture = response.getJSONObject("event").getString("PICTURE").hexToByteArray()
                )
                fetchEventFromServerState.value = ServerValidationState.Success(response)
            },
            { error ->
                fetchEventFromServerState.value = ServerValidationState.Failure(error)
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

    fun increaseTickets() {
        if (numberTickets < 4) {
            numberTickets++
        }
    }

    fun decreaseTickets() {
        if (numberTickets > 0) {
            numberTickets--
        }
    }

    fun checkout() {
        purchaseTicketsInServerState.value = ServerValidationState.Loading("Purchasing tickets...")
        // Send order to server
        val endpoint = "buy_ticket"
        // Create the request body
        val json = objectToJson(
            ticketPurchaseMessage(
                getUserIdInSharedPreferences(context),
                eventId,
                numberTickets,
            )
        )

        // Create the request
        val request = JsonObjectRequest(
            Request.Method.POST, serverUrl + endpoint, json,
            { response ->
                storeTicketsAndVouchersInDatabase(response)
                purchaseTicketsInServerState.value = ServerValidationState.Success(response)
            },
            { error ->
                purchaseTicketsInServerState.value = ServerValidationState.Failure(error)
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(request)

    }

    private fun storeTicketsAndVouchersInDatabase(response: JSONObject?) {
        // Store tickets and vouchers in database
        val tickets = response?.getJSONArray("tickets")
        val vouchers = response?.getJSONArray("vouchers")
        val purchase = response?.getJSONObject("purchase")

        // Store tickets
        for (i in 0 until tickets!!.length()) {
            val ticket = tickets.getJSONObject(i)
            viewModelScope.launch {
                ticketoStorage.insertTicket(
                    Ticket(
                        ticket_id = ticket.getString("ticket_id"),
                        purchase_id = ticket.getInt("purchase_id"),
                        event_id = ticket.getInt("event_id"),
                        purchase_date = formatDate(ticket.getString("purchase_date")),
                        used = ticket.getInt("used") != 0,
                        place = ticket.getInt("place").toString()
                    )
                )
            }
        }

        // Store vouchers
        for (i in 0 until vouchers!!.length()) {
            val voucher = vouchers.getJSONObject(i)
            viewModelScope.launch {
                ticketoStorage.insertVoucher(
                    Voucher(
                        voucher_id = voucher.getString("voucher_id"),
                        customer_id = voucher.getString("customer_id"),
                        product_id = voucher.optInt("product_id"),
                        order_id = null,
                        type = voucher.getString("type"),
                        description = voucher.getString("description"),
                        redeemed = voucher.getInt("redeemed") != 0
                    )
                )
            }
        }

        // Store purchase
        viewModelScope.launch {
            ticketoStorage.insertPurchase(
                Purchase(
                    purchase_id = purchase!!.getInt("purchase_id"),
                    customer_id = purchase.getString("customer_id"),
                    total_price = purchase.getDouble("total_price").toFloat(),
                    date = formatDate(purchase.getString("date"))
                )
            )
        }
    }
}
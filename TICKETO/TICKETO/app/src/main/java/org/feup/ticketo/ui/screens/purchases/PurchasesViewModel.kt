package org.feup.ticketo.ui.screens.purchases

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Purchase
import org.feup.ticketo.data.storage.PurchaseWithTicketsAndEventsAndVouchers
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.Voucher
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences
import org.feup.ticketo.utils.formatDate
import org.feup.ticketo.utils.serverUrl

class PurchasesViewModel(
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {

    val fetchPurchasesFromServerState = mutableStateOf<ServerValidationState?>(null)

    val purchases = mutableStateOf<List<PurchaseWithTicketsAndEventsAndVouchers>>(emptyList())

    val customerName = mutableStateOf("")
    val taxNumber = mutableStateOf("")

    fun fetchPurchases() {
        fetchPurchasesFromServerState.value = ServerValidationState.Loading("Loading purchases...")
        fetchPurchasesFromServer()
        viewModelScope.launch {
            customerName.value = ticketoStorage.getCustomer(getUserIdInSharedPreferences(context)).username.orEmpty()
            if (customerName.value.isEmpty())
                customerName.value = ticketoStorage.getCustomer(getUserIdInSharedPreferences(context)).username.toString()
            taxNumber.value =
                ticketoStorage.getCustomer(getUserIdInSharedPreferences(context)).tax_number.toString()
        }

    }

    private fun overwriteLocalData() {
        purchases.value.forEach { purchaseWithTicketsAndEventsAndVouchers ->
            // Insert or update Purchase
            val purchase = purchaseWithTicketsAndEventsAndVouchers.purchase
            Log.d("Purchase", purchase.toString())
            viewModelScope.launch {
                ticketoStorage.insertPurchase(purchase)
            }

            // Insert or update Tickets
            purchaseWithTicketsAndEventsAndVouchers.tickets.forEach { ticket ->
                viewModelScope.launch {
                    ticketoStorage.insertTicket(ticket)
                }
            }

            // Insert or update Event
            val event = purchaseWithTicketsAndEventsAndVouchers.event
            viewModelScope.launch {
                ticketoStorage.insertEvent(event)
            }

            // Insert or update Vouchers
            purchaseWithTicketsAndEventsAndVouchers.vouchers.forEach { voucher ->
                viewModelScope.launch {
                    if (ticketoStorage.getVoucherById(voucher.voucher_id)?.order_id == null || ticketoStorage.getVoucherById(voucher.voucher_id)?.redeemed == true)
                        ticketoStorage.insertVoucher(voucher)
                }
            }
        }

        // Delete used tickets
        viewModelScope.launch {
            ticketoStorage.deleteUsedTicketsForCustomer(getUserIdInSharedPreferences(context))
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun fetchPurchasesFromServer() {
        val url = serverUrl + "purchases?customer_id=" + getUserIdInSharedPreferences(context)
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val purchasesJsonArray = response.getJSONArray("purchases")
                val purchasesList = mutableListOf<PurchaseWithTicketsAndEventsAndVouchers>()
                for (i in 0 until purchasesJsonArray.length()) {
                    val purchaseJson = purchasesJsonArray.getJSONObject(i)
                    val purchase = Purchase(
                        purchase_id = purchaseJson.getInt("PURCHASE_ID"),
                        customer_id = purchaseJson.getString("CUSTOMER_ID"),
                        total_price = purchaseJson.getDouble("TOTAL_PRICE").toFloat(),
                        date = formatDate(purchaseJson.getString("DATE")) // assuming you have a formatDate function
                    )

                    val ticketsJsonArray = purchaseJson.getJSONArray("tickets")
                    val ticketsList = mutableListOf<Ticket>()
                    for (j in 0 until ticketsJsonArray.length()) {
                        val ticketJson = ticketsJsonArray.getJSONObject(j)
                        val ticket = Ticket(
                            ticket_id = ticketJson.getString("TICKET_ID"),
                            purchase_id = ticketJson.getInt("PURCHASE_ID"),
                            event_id = ticketJson.getInt("EVENT_ID"),
                            purchase_date = formatDate(ticketJson.getString("PURCHASE_DATE")),
                            used = ticketJson.getInt("USED") != 0,
                            qrcode = ticketJson.getString("QRCODE"),
                            place = ticketJson.getString("PLACE")
                        )
                        ticketsList.add(ticket)
                    }

                    val eventJson = purchaseJson.getJSONObject("event_details")
                    val event = Event(
                        event_id = eventJson.getInt("EVENT_ID"),
                        name = eventJson.getString("NAME"),
                        date = formatDate(eventJson.getString("DATE")),
                        picture = eventJson.getString("PICTURE").hexToByteArray(),
                        price = eventJson.getDouble("PRICE").toFloat()
                    )

                    val vouchersJsonArray = purchaseJson.getJSONArray("vouchers")
                    val vouchersList = mutableListOf<Voucher>()
                    for (k in 0 until vouchersJsonArray.length()) {
                        val voucherJson = vouchersJsonArray.getJSONObject(k)
                        val voucher = Voucher(
                            voucher_id = voucherJson.getString("VOUCHER_ID"),
                            customer_id = voucherJson.getString("CUSTOMER_ID"),
                            purchase_id = voucherJson.getInt("PURCHASE_ID"),
                            product_id = if (!voucherJson.isNull("PRODUCT_ID")) voucherJson.getInt("PRODUCT_ID") else null,
                            order_id = if (!voucherJson.isNull("ORDER_ID")) voucherJson.getInt("ORDER_ID") else null,
                            type = voucherJson.getString("TYPE"),
                            description = voucherJson.getString("DESCRIPTION"),
                            redeemed = voucherJson.getInt("REDEEMED") != 0
                        )
                        vouchersList.add(voucher)
                    }

                    val purchaseWithTicketsAndEventsAndVouchers =
                        PurchaseWithTicketsAndEventsAndVouchers(
                            purchase,
                            ticketsList,
                            event,
                            vouchersList
                        )
                    purchasesList.add(purchaseWithTicketsAndEventsAndVouchers)
                }
                purchases.value = purchasesList
                overwriteLocalData()
                fetchPurchasesFromServerState.value = ServerValidationState.Success(response)
            },
            { error ->
                fetchPurchasesFromServerState.value = ServerValidationState.Failure(error)
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}
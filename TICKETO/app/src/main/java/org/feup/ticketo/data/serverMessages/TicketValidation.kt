package org.feup.ticketo.data.serverMessages

import kotlinx.serialization.Serializable
import org.feup.ticketo.data.storage.Customer
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.utils.signMessageWithPrivateKey

@Serializable
data class TicketValidationMessage(
    val customer_id: String?,
    val tickets: List<MutableMap<String, String>>,
    var signature: String?=null
)

fun ticketValidationMessage(
    customer: Customer,
    tickets: List<Ticket>,
    signature: String?
): TicketValidationMessage {
    var tvm = TicketValidationMessage(
        customer.customer_id,
        tickets = tickets.map { mutableMapOf("ticket_id" to it.ticket_id.orEmpty()) },
        null
    )
    return signMessageWithPrivateKey(tvm)
}
package org.feup.ticketo.data.serverMessages

import kotlinx.serialization.Serializable
import org.feup.ticketo.utils.signMessageWithPrivateKey

@Serializable
data class TicketPurchaseMessage (
    val customer_id: String?,
    val event_id: Int?,
    val nr_of_tickets: Int?,
    val signature: String?=null
)

fun ticketPurchaseMessage(
    customer_id: String?,
    event_id: Int?,
    nr_of_tickets: Int?
): TicketPurchaseMessage {

    var tpm = TicketPurchaseMessage(customer_id, event_id, nr_of_tickets)
    return signMessageWithPrivateKey(tpm)

}
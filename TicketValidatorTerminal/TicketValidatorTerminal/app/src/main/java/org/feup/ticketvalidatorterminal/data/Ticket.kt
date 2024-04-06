package org.feup.ticketvalidatorterminal.data

data class Ticket(
    val id: String?,
    val purchase_id: String?,
    val event_id: String?,
    val purchase_date: String?,
    val used: String?,
    val place: String?
)


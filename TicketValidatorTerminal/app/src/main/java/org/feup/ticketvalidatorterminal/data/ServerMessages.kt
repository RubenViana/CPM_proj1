package org.feup.ticketvalidatorterminal.data

import kotlinx.serialization.Serializable

@Serializable
data class TicketValidationMessage(
    val customer_id: String?,
    val tickets: List<MutableMap<String, String>>,
    val signature: String
)
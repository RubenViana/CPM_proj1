package org.feup.ticketo.data

import kotlinx.serialization.Serializable

@Serializable
data class TicketValidationMessage(
    val customer_id: String?,
    val tickets: List<MutableMap<String, String>>,
    val signature: String
)

@Serializable
data class OrderValidationMessage(
    val customer_id: String?,
    val products: List<MutableMap<String, String>>,
    val vouchers: List<MutableMap<String, String>>,
    val signature: String
)

@Serializable
data class OrderProductQuantity(
    val product_id: Product,
    val quantity: Int
)

@Serializable
data class OrderValidationResponse(
    val message: String,
    val total_price: Double,
    val order_id: Int,
    val products: List<MutableMap<String, String>>,
    val vouchers: List<MutableMap<String, String>>,
    val tax_number: Int
)

fun ticketValidationMessage(
    customer: Customer,
    tickets: List<Ticket>,
    signature: String
): TicketValidationMessage {
    return TicketValidationMessage(
        customer.customer_id,
        tickets = tickets.map { mutableMapOf("ticket_id" to it.ticket_id.orEmpty()) },
        signature
    )
}

fun orderValidationMessage(
    customer: Customer,
    products: List<OrderProductQuantity>,
    vouchers: List<Voucher>,
    signature: String
): OrderValidationMessage {
    return OrderValidationMessage(
        customer_id = customer.customer_id,
        products = products.map {
            mutableMapOf(
                "product_id" to it.product_id.toString(),
                "quantity" to it.quantity.toString()
            )
        },
        vouchers = vouchers.map {
            mutableMapOf(
                "voucher_id" to it.voucher_id.toString(),
                "product_id" to it.product_id.toString()
            )
        },
        signature = signature
    )
}
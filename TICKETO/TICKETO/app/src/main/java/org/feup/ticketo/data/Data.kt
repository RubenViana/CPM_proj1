package org.feup.ticketo.data

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val customer_id: String? = null,
    val username: String? = null,
    val password: String? = null,
    val tax_number: Long? = null,
    val public_key: String? = null,
    val name: String? = null
)

@Serializable
data class CreditCard(
    val credit_card_id: Int? = null,
    val customer_id: String? = null,
    val type: String? = null,
    val number: String? = null,
    val validity: String? = null
)

@Serializable
data class Event(
    val event_id: Int,
    val name: String,
    val date: String,
    val picture: ByteArray,
    val price: Float
)

@Serializable
data class Order(
    val order_id: Int? = null,
    val customer_id: String? = null,
    val date: String? = null,
    val paid: Boolean? = null,
    val picked_up: Boolean? = null,
    val total_price: Float? = null
)

@Serializable
data class Product(
    val product_id: Int? = null,
    val available: Boolean? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Float? = null
)

@Serializable
data class OrderProduct(
    val product_id: Int? = null,
    val order_id: Int? = null,
    val quantity: Int? = null
)

@Serializable
data class Purchase(
    val purchase_id: Int? = null,
    val customer_id: String? = null,
    val date: String? = null,
    val total_price: Float? = null
)

@Serializable
data class Ticket(
    val ticket_id: String,
    val purchase_id: Int,
    val event_id: Int,
    val purchase_date: String,
    val used: Boolean,
    val qrcode: String,
    val place: String
)

@Serializable
data class Voucher(
    val voucher_id: String? = null,
    val customer_id: String? = null,
    val product_id: Int? = null,
    val order_id: Int? = null,
    val type: String? = null,
    val description: String? = null,
    val redeemed: Boolean? = null
)
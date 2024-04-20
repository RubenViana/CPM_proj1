package org.feup.ticketo.data.serverMessages

import kotlinx.serialization.Serializable
import org.feup.ticketo.data.storage.Customer
import org.feup.ticketo.data.storage.OrderProductWithProduct
import org.feup.ticketo.data.storage.Voucher

@Serializable
data class OrderValidationMessage(
    val customer_id: String?,
    val products: List<MutableMap<String, String>>,
    val vouchers: List<MutableMap<String, String>>,
    var signature: String?=null
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

fun orderValidationMessage(
    customer: Customer,
    products: List<OrderProductWithProduct>,
    vouchers: List<Voucher>,
    signature: String?
): OrderValidationMessage {
    return OrderValidationMessage(
        customer_id = customer.customer_id,
        products = products.map {
            mutableMapOf(
                "product_id" to it.orderProduct.product_id.toString(),
                "quantity" to it.orderProduct.quantity.toString()
            )
        },
        vouchers = vouchers.map {
            mutableMapOf(
                "voucher_id" to it.voucher_id.orEmpty(),
                "product_id" to it.product_id.toString()
            )
        },
        signature = signature
    )
}
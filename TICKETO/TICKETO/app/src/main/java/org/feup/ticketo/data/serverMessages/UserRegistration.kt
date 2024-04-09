package org.feup.ticketo.data.serverMessages

import kotlinx.serialization.Serializable
import org.feup.ticketo.data.storage.CreditCard
import org.feup.ticketo.data.storage.Customer

@Serializable
data class CustomerRegistrationMessage(
    val name: String,
    val username: String,
    val password: String,
    val tax_number: Int,
    val public_key: String,
    val credit_card_number: String,
    val credit_card_validity: String,
    val credit_card_type: String
)

fun customerRegistrationMessage(customer: Customer, creditCard: CreditCard): CustomerRegistrationMessage {
    return CustomerRegistrationMessage(
        customer.customer_id,
        customer.username.orEmpty(),
        customer.password.orEmpty(),
        customer.tax_number ?: 0,
        customer.public_key.orEmpty(),
        creditCard.number.orEmpty(),
        creditCard.validity.orEmpty(),
        creditCard.type.orEmpty()
    )
}
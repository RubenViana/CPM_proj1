package org.feup.cafeteriaorderterminal.data

import kotlinx.serialization.Serializable

@Serializable
data class OrderValidationMessage(
    val customer_id: String?,
    val products: List<MutableMap<String, String>>,
    val vouchers: List<MutableMap<String, String>>,
    val signature: String
)
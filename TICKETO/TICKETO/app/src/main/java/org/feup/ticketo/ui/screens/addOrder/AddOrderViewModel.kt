package org.feup.ticketo.ui.screens.addOrder

import android.content.Context
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Order
import org.feup.ticketo.data.storage.OrderProduct
import org.feup.ticketo.data.storage.Product
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.Voucher
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences
import java.text.SimpleDateFormat
import java.util.Date

class AddOrderViewModel(
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {

    val fetchMenuFromStorageState = mutableStateOf<ServerValidationState?>(null)
    val fetchVouchersFromStorageState = mutableStateOf<ServerValidationState?>(null)
    val orderCheckoutStatus = mutableStateOf<ServerValidationState?>(null)
    val menu = mutableStateOf<List<Product>>(emptyList())
    val orderProducts = MutableStateFlow<List<OrderProduct>>(emptyList())
    val vouchers = mutableStateOf<List<Voucher>>(emptyList())
    val orderVouchers = mutableStateOf<List<Voucher>>(emptyList())
    val totalPrice = mutableFloatStateOf(0f)
    val subTotalPrice = mutableFloatStateOf(0f)
    val openOrderConfirmationDialog = mutableStateOf(false)

//    init {
//        viewModelScope.launch {
//            orderProducts.collect {
//                calculateTotalPrice()
//            }
//        }
//        viewModelScope.launch {
//            orderVouchers.collect {
//                calculateTotalPrice()
//            }
//        }
//    }

    private fun calculateTotalPrice() {
        var total = orderProducts.value.sumOf { orderProduct ->
            val product = menu.value.find { it.product_id == orderProduct.product_id }
            product?.price?.times(orderProduct.quantity ?: 0)?.toDouble() ?: 0.0
        }

        subTotalPrice.value = total.toFloat()

        // Adjust total price based on vouchers
        orderVouchers.value.filterNot { it.description == "Discount of 5% for the next purchase" }.forEach { voucher ->
            total = applyVoucherDiscount(voucher, total)
        }

        orderVouchers.value.firstOrNull { it.description == "Discount of 5% for the next purchase" }?.let { voucher ->
            total = applyVoucherDiscount(voucher, total)
        }

        totalPrice.value = total.toFloat()
    }

    private fun applyVoucherDiscount(voucher: Voucher, total: Double): Double {
        return when (voucher.description) {
            "Free Coffee for buying a ticket" -> {
                // Adjust totalPrice accordingly
                // For example:
                // If buying a coffee costs 2.5 €, reduce totalPrice by 2.5 €
                total - 2.5
            }
            "Free Popcorn for buying a ticket" -> {
                // Adjust totalPrice accordingly
                // For example:
                // If buying a popcorn costs 3 €, reduce totalPrice by 3 €
                total - 3.0
            }
            "Discount of 5% for the next purchase" -> {
                // Adjust totalPrice by applying a 5% discount
                total * 0.95
            }
            else -> total // No voucher discount applied
        }
    }

    fun fetchMenu() {
        fetchMenuFromStorageState.value = ServerValidationState.Loading("Loading menu...")
        fetchMenuFromStorage()
    }

    private fun fetchMenuFromStorage() {
        viewModelScope.launch {
            menu.value = ticketoStorage.getAllProducts()
        }
        fetchMenuFromStorageState.value = ServerValidationState.Success(null, "Menu loaded")
    }

    fun fetchVouchers() {
        fetchVouchersFromStorageState.value = ServerValidationState.Loading("Loading vouchers...")
        fetchVouchersFromStorage()
    }

    private fun fetchVouchersFromStorage() {
        viewModelScope.launch {
            vouchers.value =
                ticketoStorage.getUnusedVouchersForCustomer(getUserIdInSharedPreferences(context))
        }
        fetchVouchersFromStorageState.value =
            ServerValidationState.Success(null, "Vouchers loaded")
    }

    fun increaseProductQuantity(product: Product) {
        // Check if product is already in order
        if (orderProducts.value.find { it.product_id == product.product_id } == null) {
            orderProducts.value += OrderProduct(product.product_id, -1, 1)
        } else {
            orderProducts.value = orderProducts.value.map {
                if (it.product_id == product.product_id) {
                    it.copy(quantity = it.quantity?.plus(1))
                } else {
                    it
                }
            }
        }
    }

    fun decreaseProductQuantity(product: Product) {
        // Check if current product quantity is 1
        if (orderProducts.value.find { it.product_id == product.product_id }?.quantity == 1) {
            orderProducts.value = orderProducts.value.filter { it.product_id != product.product_id }
        } else {
            orderProducts.value = orderProducts.value.map {
                if (it.product_id == product.product_id) {
                    it.copy(quantity = it.quantity?.minus(1))
                } else {
                    it
                }
            }
        }
    }

    fun handleVoucherOnCheckedChange(voucher: Voucher) {
        if (orderVouchers.value.contains(voucher)) {
            orderVouchers.value = orderVouchers.value.filter { it != voucher }
        } else if (orderVouchers.value.size < 2) {
            orderVouchers.value += voucher
        }
    }

    fun checkout() {
        orderCheckoutStatus.value = ServerValidationState.Loading("Placing order...")
        try {

            // create order in local database
            viewModelScope.launch {
                // Create order
                val order_id = ticketoStorage.getMaxOrderId()?.plus(1) ?: 1
                val order = Order(
                    order_id = order_id,
                    customer_id = getUserIdInSharedPreferences(context),
                    date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                    paid = false,
                    picked_up = false,
                    total_price = totalPrice.value
                )
                ticketoStorage.insertOrder(order)
                orderProducts.value.forEach { orderProduct ->
                    ticketoStorage.insertOrderProduct(orderProduct.copy(order_id = order_id))
                }

                // associate vouchers with order
                orderVouchers.value.forEach { voucher ->
                    ticketoStorage.insertVoucher(voucher.copy(order_id = order_id))
                }

            }

            orderCheckoutStatus.value = ServerValidationState.Success(null, "Order placed successfully!")
        } catch (e: Exception) {
            orderCheckoutStatus.value =
                ServerValidationState.Failure(null, "Failed to create order")
        }
    }
}
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
    val total_price = mutableFloatStateOf(0f)

    init {
        viewModelScope.launch {
            orderProducts.collect {
                total_price.value = orderProducts.value.sumOf { orderProduct: OrderProduct ->
                    val product = menu.value.find { it.product_id == orderProduct.product_id }
                    product?.price?.times(orderProduct.quantity ?: 0)?.toDouble()
                        ?: 0f.toDouble()
                }.toFloat()
            }
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
        fetchMenuFromStorageState.value = ServerValidationState.Loading("Loading vouchers...")
        fetchVouchersFromStorage()
    }

    private fun fetchVouchersFromStorage() {
        try {
            viewModelScope.launch {
                vouchers.value =
                    ticketoStorage.getUnusedVouchersForCustomer(getUserIdInSharedPreferences(context))
            }
            fetchVouchersFromStorageState.value =
                ServerValidationState.Success(null, "Vouchers loaded")
        } catch (e: Exception) {
            fetchVouchersFromStorageState.value =
                ServerValidationState.Failure(null, "Failed to load vouchers")
        }
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
                    total_price = total_price.value
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

            orderCheckoutStatus.value = ServerValidationState.Success(null, "Order placed")
        } catch (e: Exception) {
            orderCheckoutStatus.value =
                ServerValidationState.Failure(null, "Failed to create order")
        }
    }

    fun productQuantity(product: Product): String {
        return orderProducts.value.find { it.product_id == product.product_id }?.quantity?.toString()
            ?: "0"
    }


}
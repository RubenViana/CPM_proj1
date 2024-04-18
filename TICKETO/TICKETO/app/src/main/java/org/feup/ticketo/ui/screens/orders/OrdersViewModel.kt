package org.feup.ticketo.ui.screens.orders

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Order
import org.feup.ticketo.data.storage.Products.products
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences


class OrdersViewModel(private val context: Context, private val ticketoStorage: TicketoStorage) :
    ViewModel() {

    val fetchOrdersFromDatabaseState = mutableStateOf<ServerValidationState?>(null)

    val orders = mutableStateOf<List<Order>>(emptyList())

    init {
        // Insert products in database
        viewModelScope.launch {
            products.forEach {
                ticketoStorage.insertProduct(it)
            }
        }
    }

    fun fetchOrders() {
        fetchOrdersFromDatabaseState.value = ServerValidationState.Loading("Loading orders...")
        fetchOrdersFromDatabase()
    }

    private fun fetchOrdersFromDatabase() {
        viewModelScope.launch {
            orders.value = ticketoStorage.getUnpickedUpOrdersForClient(
                getUserIdInSharedPreferences(context)
            )
            fetchOrdersFromDatabaseState.value = ServerValidationState.Success(null, null)
        }
    }
}
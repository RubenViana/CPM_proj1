package org.feup.ticketo.ui.screens.pastOrders

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Order
import org.feup.ticketo.data.storage.OrderProduct
import org.feup.ticketo.data.storage.OrderProductWithProduct
import org.feup.ticketo.data.storage.OrderWithProductsAndQuantityAndVouchers
import org.feup.ticketo.data.storage.Product
import org.feup.ticketo.data.storage.Purchase
import org.feup.ticketo.data.storage.PurchaseWithTicketsAndEventsAndVouchers
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.Voucher
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences
import org.feup.ticketo.utils.formatDate
import org.feup.ticketo.utils.serverUrl

class PastOrdersViewModel(
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {
    val fetchOrdersFromServerState = mutableStateOf<ServerValidationState?>(null)

    val orders = mutableStateOf<List<OrderWithProductsAndQuantityAndVouchers>>(emptyList())

    val customerName = mutableStateOf("")
    val taxNumber = mutableStateOf("")

    fun fetchOrders() {
        fetchOrdersFromServerState.value = ServerValidationState.Loading("Loading orders...")
        fetchOrdersFromServer()
        viewModelScope.launch {
            customerName.value =
                ticketoStorage.getCustomer(getUserIdInSharedPreferences(context)).username.orEmpty()
            if (customerName.value.isEmpty())
                customerName.value =
                    ticketoStorage.getCustomer(getUserIdInSharedPreferences(context)).username.toString()
            taxNumber.value =
                ticketoStorage.getCustomer(getUserIdInSharedPreferences(context)).tax_number.toString()
        }

    }


    private fun overwriteLocalData() {
    }

    private fun fetchOrdersFromServer() {
        val url = serverUrl + "orders?customer_id=" + getUserIdInSharedPreferences(context)
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val ordersJsonArray = response.getJSONArray("orders")
                val ordersList = mutableListOf<OrderWithProductsAndQuantityAndVouchers>()
                for (i in 0 until ordersJsonArray.length()) {
                    val orderJson = ordersJsonArray.getJSONObject(i)
                    val order = Order(
                        order_id = orderJson.getInt("ORDER_ID"),
                        customer_id = orderJson.getString("CUSTOMER_ID"),
                        total_price = orderJson.getDouble("TOTAL_PRICE").toFloat(),
                        date = formatDate(orderJson.getString("DATE")),
                        paid = orderJson.isNull("PAID") || orderJson.getInt("PAID") != 0,
                        picked_up = orderJson.getInt("PICKED_UP") != 0
                    )

                    val productsJsonArray = orderJson.getJSONArray("products")
                    val orderProductsList = mutableListOf<OrderProductWithProduct>()
                    for (j in 0 until productsJsonArray.length()) {
                        val productJson = productsJsonArray.getJSONObject(j)
                        val orderProduct = OrderProduct(
                            order_id = order.order_id,
                            product_id = productJson.getInt("PRODUCT_ID"),
                            quantity = productJson.getInt("QUANTITY")
                        )
                        val product = Product(
                            product_id = productJson.getInt("PRODUCT_ID"),
                            name = productJson.getString("NAME"),
                            description = productJson.getString("DESCRIPTION"),
                            price = productJson.getDouble("PRICE").toFloat(),
                            available = productJson.getInt("AVAILABLE") != 0
                        )
                        orderProductsList.add(OrderProductWithProduct(orderProduct, product))
                    }

                    val vouchersJsonArray = orderJson.getJSONArray("vouchers")
                    val vouchersList = mutableListOf<Voucher>()
                    for (k in 0 until vouchersJsonArray.length()) {
                        val voucherJson = vouchersJsonArray.getJSONObject(k)
                        val voucher = Voucher(
                            voucher_id = voucherJson.getString("VOUCHER_ID"),
                            customer_id = voucherJson.getString("CUSTOMER_ID"),
                            purchase_id = if (!voucherJson.isNull("PURCHASE_ID")) voucherJson.getInt("PURCHASE_ID") else null,
                            product_id = if (!voucherJson.isNull("PRODUCT_ID")) voucherJson.getInt("PRODUCT_ID") else null,
                            order_id = if (!voucherJson.isNull("ORDER_ID")) voucherJson.getInt("ORDER_ID") else null,
                            type = voucherJson.getString("TYPE"),
                            description = voucherJson.getString("DESCRIPTION"),
                            redeemed = voucherJson.getInt("REDEEMED") != 0
                        )
                        vouchersList.add(voucher)
                    }

                    val orderWithProductsAndQuantityAndVouchers = OrderWithProductsAndQuantityAndVouchers(
                        order,
                        orderProductsList,
                        vouchersList
                    )
                    ordersList.add(orderWithProductsAndQuantityAndVouchers)
                }
                orders.value = ordersList
//                overwriteLocalData()
                fetchOrdersFromServerState.value = ServerValidationState.Success(response)
            },
            { error ->
                fetchOrdersFromServerState.value = ServerValidationState.Failure(error)
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}
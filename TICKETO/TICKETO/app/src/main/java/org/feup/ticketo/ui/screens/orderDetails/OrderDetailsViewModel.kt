package org.feup.ticketo.ui.screens.orderDetails

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.serverMessages.orderValidationMessage
import org.feup.ticketo.data.storage.Customer
import org.feup.ticketo.data.storage.OrderWithProductsAndQuantityAndVouchers
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences
import org.feup.ticketo.utils.generateQRCode

class OrderDetailsViewModel(
    val orderId: Int,
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {


    val fetchOrderFromDatabaseState = mutableStateOf<ServerValidationState?>(null)
    val qrCodeGenerationState = mutableStateOf<ServerValidationState?>(null)
    val openOrderValidationConfirmationDialog = mutableStateOf(false)
    val order = mutableStateOf<OrderWithProductsAndQuantityAndVouchers?>(null)
    val qrCode = mutableStateOf<Bitmap?>(null)
    fun fetchOrder() {
        fetchOrderFromDatabaseState.value = ServerValidationState.Loading("Loading order...")
        fetchOrderFromDatabase()
    }

    private fun fetchOrderFromDatabase() {
        viewModelScope.launch {
            order.value = ticketoStorage.getOrderDetails(
                getUserIdInSharedPreferences(context),
                orderId
            )
        }
        fetchOrderFromDatabaseState.value = ServerValidationState.Success(null, "Order Loaded!")
    }

    fun validateOrder() {
        qrCodeGenerationState.value = ServerValidationState.Loading("Generating QR Code...")
        val ovm = orderValidationMessage(
            Customer(getUserIdInSharedPreferences(context)),
            order.value!!.orderProducts,
            order.value!!.vouchers,
            null
        )

        qrCode.value = generateQRCode(ovm)
        if (qrCode.value != null) {
            // Update order
            order.value!!.vouchers.forEach {
                viewModelScope.launch {
                    ticketoStorage.deleteVoucherById(it.voucher_id)
                }
            }
            viewModelScope.launch {
                ticketoStorage.setOrderAsPickedUp(orderId)
            }
            qrCodeGenerationState.value =
                ServerValidationState.Success(null, "QR code generated successfully!")
        } else {
            qrCodeGenerationState.value =
                ServerValidationState.Failure(null, "Error generating QR code")
        }


    }
}
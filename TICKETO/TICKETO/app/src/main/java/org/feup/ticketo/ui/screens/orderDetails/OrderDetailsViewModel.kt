package org.feup.ticketo.ui.screens.orderDetails

import android.content.Context
import org.feup.ticketo.data.storage.TicketoStorage

class OrderDetailsViewModel(
    private val orderId: Int,
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) {
}
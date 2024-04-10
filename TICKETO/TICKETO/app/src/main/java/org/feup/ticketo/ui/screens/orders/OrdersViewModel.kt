package org.feup.ticketo.ui.screens.orders

import android.content.Context
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import org.feup.ticketo.data.storage.TicketoDatabase

class OrdersViewModel(
    private val context: Context
) : ViewModel() {
    private val db = TicketoDatabase.getDatabase(context = context)

    fun getMenuItems(): List<MenuItem> {
        return db.ticketoDao().getAllProducts().map {
            MenuItem(
                productId = it.product_id ?: 0L,
                name = it.name ?: "",
                description = it.description ?: "",
                price = it.price ?: 0.0f
            )
        }
    }
}
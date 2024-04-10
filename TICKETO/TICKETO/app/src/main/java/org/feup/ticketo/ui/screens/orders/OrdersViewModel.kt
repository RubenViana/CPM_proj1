package org.feup.ticketo.ui.screens.orders

import android.content.Context
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Product
import org.feup.ticketo.data.storage.TicketoDatabase

class OrdersViewModel(
    private val productId: Int,
    private val context: Context
) : ViewModel() {
    private val db = TicketoDatabase.getDatabase(context = context)

    fun getProductItems(): ProductItem {
       val product = getProduct()
        return ProductItem(
            product?.name,
            product?.description,
            product?.price
        )
    }

    private fun getProduct(): Product? {
        return db.ticketDao().getProduct(productId)
    }

}

data class ProductItem(
    val name: String?,
    val description: String?,
    val price: Float?
)
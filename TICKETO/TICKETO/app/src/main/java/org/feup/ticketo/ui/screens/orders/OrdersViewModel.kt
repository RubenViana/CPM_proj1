package org.feup.ticketo.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.room.ColumnInfo
import org.feup.ticketo.data.storage.Product

class OrdersViewModel(
    //private val productId: Int,
    //private val context: Context
) : ViewModel() {
   // private val db = TicketoDatabase.getDatabase(context = context)

    fun getProductItems(): List<ProductItem> {
        //val products = db.ticketDao().getAllProducts()
        /*return products.map { product ->
            ProductItem(
                product.name,
                product.description,
                product.price
            )
        }*/
        return listOf(
            ProductItem(
                1,
                "Coca Cola",
                "Drink",
                2.5f
            )
        )
    }

   /* private fun getProduct(): Product? {
        return db.ticketDao().getProduct(productId)
    }*/


    fun checkout() {
        // checkout orders
        // communication with server
        //Orders Validation Message
    }

}

data class ProductItem(
    val product_id: Int?,
    val name: String?,
    val description: String?,
    val price: Float?
)
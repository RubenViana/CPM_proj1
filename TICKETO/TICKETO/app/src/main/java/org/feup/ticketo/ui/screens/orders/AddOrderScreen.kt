package org.feup.ticketo.ui.screens.orders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(navController: NavHostController, viewModel: OrdersViewModel) {
    val productItems = viewModel.getProductItems()

    // MutableState for keeping track of selected items
    val selectedItems = remember { mutableStateListOf<ProductItem>() }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn {
            items(productItems) { productItem ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedItems.contains(productItem),
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                selectedItems.add(productItem)
                            } else {
                                selectedItems.remove(productItem)
                            }
                        }
                    )
                    Text(
                        text = productItem.name ?: "",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // Display total price
        val totalPrice = selectedItems.sumOf { it.price?.toDouble() ?: 0.0 }
        Text(
            text = "Total Price: $totalPrice",
            modifier = Modifier.padding(16.dp)
        )

        // "Confirm Order" button
        Button(
            onClick = {
                // TODO: Implement order confirmation logic
                navController.popBackStack()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Confirm Order")
        }
    }

}



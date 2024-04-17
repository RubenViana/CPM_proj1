package org.feup.ticketo.ui.screens.orders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.feup.ticketo.ui.screens.orders.OrdersViewModel
import org.feup.ticketo.ui.screens.orders.ProductItem
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(navController: NavHostController, viewModel: OrdersViewModel) {
    // Composable content that uses the callback

    /*val productItems = viewModel.getProductItems()

    // MutableState for keeping track of selected items
    val selectedItems = remember { mutableStateListOf<ProductItem>() }

   /* Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = md_theme_light_primary,
                actionIconContentColor = md_theme_light_onPrimary,
                navigationIconContentColor = md_theme_light_onPrimary,
                titleContentColor = md_theme_light_onPrimary,
                scrolledContainerColor = md_theme_light_primary
            ),
            title = { Text("Cafeteria Menu") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back")
                }
            },
            actions = {
                IconButton(onClick = { navController.navigate("newOrder") }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Order")
                }
            }
        )*/

        // Display menu items with checkboxes
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
        }*/
}





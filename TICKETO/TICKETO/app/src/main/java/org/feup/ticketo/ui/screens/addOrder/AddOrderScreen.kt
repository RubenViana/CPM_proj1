package org.feup.ticketo.ui.screens.addOrder

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(navController: NavHostController, viewModel: AddOrderViewModel) {
//    val productItems = viewModel.getProductItems()
//
//    // MutableState for keeping track of selected items
//    val selectedItems = remember { mutableStateListOf<ProductItem>() }
//    Column(
//        modifier = Modifier.fillMaxSize(),
//    ) {
//        LazyColumn {
//            items(productItems) { productItem ->
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Checkbox(
//                        checked = selectedItems.contains(productItem),
//                        onCheckedChange = { isChecked ->
//                            if (isChecked) {
//                                selectedItems.add(productItem)
//                            } else {
//                                selectedItems.remove(productItem)
//                            }
//                        }
//                    )
//                    Text(
//                        text = productItem.name ?: "",
//                        modifier = Modifier.padding(horizontal = 8.dp)
//                    )
//                }
//            }
//        }
//
//        // Display total price
//        val totalPrice = selectedItems.sumOf { it.price?.toDouble() ?: 0.0 }
//        Text(
//            text = "Total Price: $totalPrice",
//            modifier = Modifier.padding(16.dp)
//        )
//
//        // "Confirm Order" button
//        Button(
//            onClick = {
//                // TODO: Implement order confirmation logic
//                navController.popBackStack()
//            },
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(text = "Confirm Order")
//        }
//    }

}



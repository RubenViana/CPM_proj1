import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
fun OrdersScreen(
    navController: NavHostController,
    viewModel: OrdersViewModel,
    onNewOrderClicked: () -> Unit // Callback for when "New Order" button is clicked
) {
    val productItems = viewModel.getProductItems()

    // MutableState for keeping track of selected items
    val selectedItems = remember { mutableStateListOf<ProductItem>() }

    Column(
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
            title = {
                Text("Cafeteria Menu")
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            action = {
                IconButton(onClick = onNewOrderClicked) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Order")
                }
            }
        )

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
        val totalPrice = selectedItems.sumOf { it.price ?: 0.0f }
        Text(
            text = "Total Price: $totalPrice",
            modifier = Modifier.padding(16.dp)
        )

        // "Confirm Order" button
        Button(
            onClick = {
                // Handle confirming the order (e.g., generate QR code)
                // For now, just navigate back to the orders screen
                onNewOrderClicked()
                navController.navigate("orders")
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Confirm Order")
        }
    }
}

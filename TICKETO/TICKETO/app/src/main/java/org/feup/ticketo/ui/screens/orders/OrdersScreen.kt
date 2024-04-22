package org.feup.ticketo.ui.screens.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Order
import org.feup.ticketo.ui.theme.md_theme_light_primary

@Composable
fun OrdersScreen(navController: NavHostController, viewModel: OrdersViewModel, modifier: Modifier) {

    LaunchedEffect(viewModel) {
        viewModel.fetchOrders()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (viewModel.fetchOrdersFromDatabaseState.value) {
            is ServerValidationState.Loading -> {
                LoadingOrders(
                    (viewModel.fetchOrdersFromDatabaseState.value as ServerValidationState.Loading).message
                        ?: "Loading orders..."
                )
            }

            is ServerValidationState.Failure -> {
                EmptyList()
            }


            is ServerValidationState.Success -> {
                if (viewModel.orders.value.isEmpty()) {
                    EmptyList()
                } else {
                    OrdersList(viewModel.orders, navController)
                }
            }
        }
    }

}

@Composable
fun OrdersList(
    orders: MutableState<List<Order>>,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(orders.value.size) { index ->
            OrderCard(order = orders.value[index], navController = navController)
        }
    }
}

@Composable
fun OrderCard(order: Order, navController: NavHostController) {
    ElevatedCard(
        modifier = Modifier.padding(10.dp),
        onClick = { navController.navigate("order/${order.order_id}") }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp)
            ) {
                Text(
                    text = "Order ID: ${order.order_id}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Date: ${order.date}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.total_price}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp

                )
                Icon(imageVector = Icons.Default.Euro, contentDescription = null)
            }

        }
    }
}


@Composable
fun LoadingOrders(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = md_theme_light_primary
        )
    }
}

@Composable
fun EmptyList() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No orders available!",
            style = TextStyle(
                color = md_theme_light_primary,
                fontSize = 22.sp
            )
        )
    }
}





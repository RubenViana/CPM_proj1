package org.feup.ticketo.ui.screens.orderDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.OrderProductWithProduct
import org.feup.ticketo.data.storage.Voucher
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(navController: NavController, viewModel: OrderDetailsViewModel) {
    LaunchedEffect(viewModel) {
        viewModel.fetchOrder()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = md_theme_light_background),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    Text("Order details")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
            )

            when (viewModel.fetchOrderFromDatabaseState.value) {
                is ServerValidationState.Loading -> {
                    LoadingOrder(
                        (viewModel.fetchOrderFromDatabaseState.value as ServerValidationState.Failure).message
                            ?: "Loading order..."
                    )
                }

                is ServerValidationState.Failure -> {
                    EmptyOrder()
                }

                is ServerValidationState.Success -> {
                    if (viewModel.order.value == null) {
                        EmptyOrder()
                    } else {
                        OrderDetails(viewModel)
                    }
                }
            }
        }
    }


}

@Composable
fun OrderDetails(viewModel: OrderDetailsViewModel) {
    ProductsList(viewModel.order.value!!.orderProducts, viewModel)
    VouchersList(viewModel.order.value!!.vouchers)
}

@Composable
fun ProductsList(products: List<OrderProductWithProduct>, viewModel: OrderDetailsViewModel) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Products")
    }
    LazyColumn {
        items(products.size) { i ->
            ProductItem(products[i], viewModel)
        }
    }
}

@Composable
fun ProductItem(orderProduct: OrderProductWithProduct, viewModel: OrderDetailsViewModel) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = md_theme_light_onPrimary
        ),
        modifier = Modifier
            .size(350.dp, 100.dp)
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp, 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Product", fontWeight = FontWeight.Bold)
                Text(
                    text = orderProduct.product.name.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Price", fontWeight = FontWeight.Bold)
                Text(
                    text = orderProduct.product.price.toString() + " €",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Quantity", fontWeight = FontWeight.Bold)
                Text(text = orderProduct.orderProduct.quantity.toString())
            }
        }
    }
}


@Composable
fun VouchersList(vouchers: List<Voucher>) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Vouchers")
    }
    LazyColumn {
        items(vouchers.size) { i ->
            VoucherItem(vouchers[i])
        }
    }
}

@Composable
fun VoucherItem(voucher: Voucher) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = voucher.type.toString())
            Text(text = voucher.description.toString())
        }
    }
}

@Composable
fun EmptyOrder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No possible to load order!",
            style = TextStyle(
                color = md_theme_light_primary,
                fontSize = 22.sp
            )
        )
    }
}

@Composable
fun LoadingOrder(s: String) {
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



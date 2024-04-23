package org.feup.ticketo.ui.screens.addOrder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Product
import org.feup.ticketo.data.storage.Voucher
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderScreen(navController: NavHostController, viewModel: AddOrderViewModel) {
    var viewModel = remember {
        viewModel
    }

    LaunchedEffect(viewModel) {
        viewModel.fetchMenu()
        viewModel.fetchVouchers()
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
                    Text("Add Order")
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
            when (viewModel.fetchMenuFromStorageState.value) {
                is ServerValidationState.Loading -> {
                    LoadingMenu(
                        (viewModel.fetchMenuFromStorageState.value as ServerValidationState.Loading).message
                    )
                }

                is ServerValidationState.Failure -> {
                    EmptyMenu()
                }

                is ServerValidationState.Success -> {
                    if (viewModel.menu.value.isEmpty()) {
                        EmptyMenu()
                    } else {
                        MenuList(viewModel)
                    }
                }
            }

            when (viewModel.fetchVouchersFromStorageState.value) {
                is ServerValidationState.Loading -> {
                    LoadingVouchers(
                        (viewModel.fetchVouchersFromStorageState.value as ServerValidationState.Loading).message
                    )
                }

                is ServerValidationState.Failure -> {
                    EmptyVouchersList()
                }

                is ServerValidationState.Success -> {
                    if (viewModel.vouchers.value.isEmpty()) {
                        EmptyVouchersList()
                    } else {
                        VouchersList(viewModel)
                    }
                }
            }

            TotalPriceAndCheckoutButton(viewModel)

            when (viewModel.orderCheckoutStatus.value) {
                is ServerValidationState.Loading -> {
                    LoadingOrderDialog(
                        (viewModel.orderCheckoutStatus.value as ServerValidationState.Loading).message
                    )
                }

                is ServerValidationState.Failure -> {
                    OrderErrorDialog(
                        viewModel.orderCheckoutStatus,
                        (viewModel.orderCheckoutStatus.value as ServerValidationState.Failure).message
                            ?: "Error placing order!"
                    )
                }

                is ServerValidationState.Success -> {
                    OrderSuccessDialog(
                        navController,
                        viewModel.orderCheckoutStatus,
                        (viewModel.orderCheckoutStatus.value as ServerValidationState.Success).message
                            ?: "Order placed successfully!"
                    )
                }
            }

            when (viewModel.openOrderConfirmationDialog.value) {
                true -> {
                    OrderConfirmationDialog(viewModel)
                }

                false -> {}
            }
        }
    }
}

@Composable
fun OrderSuccessDialog(
    navController: NavHostController,
    orderCheckoutStatus: MutableState<ServerValidationState?>,
    message: String
) {
    Dialog(
        onDismissRequest = { orderCheckoutStatus.value = null; navController.popBackStack() },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Order Completed",
                    tint = Color.Green,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = message,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun OrderErrorDialog(orderCheckoutStatus: MutableState<ServerValidationState?>, s: String) {
    Dialog(
        onDismissRequest = { orderCheckoutStatus.value = null },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Order placement failed",
                    tint = Color.Red,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = s,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun LoadingOrderDialog(s: String) {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .size(50.dp),
                    color = md_theme_light_primary,
                )
                Text(
                    text = s,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun OrderConfirmationDialog(
    viewModel: AddOrderViewModel
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Fastfood, contentDescription = null)
        },
        title = {
            Text(text = "Place Order?", textAlign = TextAlign.Center)
        },
        text = { Text("Are you sure you want to place this order?") },
        onDismissRequest = {

        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.openOrderConfirmationDialog.value = false
                }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openOrderConfirmationDialog.value = false
                    viewModel.checkout()
                }
            ) {
                Text("Confirm")
            }
        }
    )
}

@Composable
fun TotalPriceAndCheckoutButton(viewModel: AddOrderViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Sub Total",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = viewModel.subTotalPrice.floatValue.toString() + " €",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Medium
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Total",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(
//            text = viewModel.totalPrice.floatValue.toString() + " €",
            text = "TBD",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Medium
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = {
                if (viewModel.orderProducts.value.isNotEmpty()) {
                    viewModel.openOrderConfirmationDialog.value = true
                }
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Order")
        }
    }
}

@Composable
fun VouchersList(viewModel: AddOrderViewModel) {
    LazyColumn (
        modifier = Modifier.heightIn(0.dp, 250.dp)
    ){
        items(viewModel.vouchers.value.size) { i ->
            VoucherItem(viewModel.vouchers.value[i], viewModel)
        }
    }
}

@Composable
fun VoucherItem(voucher: Voucher, viewModel: AddOrderViewModel) {
    val orderVouchers = viewModel.orderVouchers.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = voucher.type.toString())
            Text(text = voucher.description.toString())
        }
        Column {
            Checkbox(
                checked = orderVouchers.value.any { it.voucher_id == voucher.voucher_id },
                onCheckedChange = { viewModel.handleVoucherOnCheckedChange(voucher) }
            )
        }

    }
}

@Composable
fun EmptyVouchersList() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "No vouchers available!")

    }
}

@Composable
fun LoadingVouchers(s: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = md_theme_light_primary
            )
            Text(text = s)
        }
    }
}

@Composable
fun LoadingMenu(s: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = md_theme_light_primary
            )
            Text(text = s)
        }
    }
}

@Composable
fun EmptyMenu() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No products available!",
            style = TextStyle(
                color = md_theme_light_primary,
                fontSize = 22.sp
            )
        )
    }
}

@Composable
fun MenuList(viewModel: AddOrderViewModel) {
    LazyColumn {
        items(viewModel.menu.value.size) { i ->
            Product(product = viewModel.menu.value[i], viewModel)
        }
    }
}

@Composable
fun Product(product: Product, viewModel: AddOrderViewModel) {
    val quantityState = remember { mutableIntStateOf(0) }

    LaunchedEffect(viewModel.orderProducts) {
        viewModel.orderProducts.collect { orderProducts ->
            val orderProduct = orderProducts.find { it.product_id == product.product_id }
            quantityState.intValue = orderProduct?.quantity ?: 0
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = product.name!!)
                Text(text = product.price.toString() + " €")
            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.decreaseProductQuantity(product) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null
                        )
                    }

                    Text(text = quantityState.intValue.toString())

                    IconButton(
                        onClick = { viewModel.increaseProductQuantity(product) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

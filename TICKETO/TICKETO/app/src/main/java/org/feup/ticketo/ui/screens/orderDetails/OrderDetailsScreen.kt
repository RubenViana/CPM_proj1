package org.feup.ticketo.ui.screens.orderDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
                    Text("Order ${viewModel.orderId} details")
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
                        OrderValidationButton(viewModel.openOrderValidationConfirmationDialog)

                    }
                }
            }

            when (viewModel.qrCodeGenerationState.value) {
                is ServerValidationState.Loading -> {
                    LoadingQRCodeDialog(
                        (viewModel.qrCodeGenerationState.value as ServerValidationState.Failure).message
                            ?: "Generating QR code..."
                    )
                }

                is ServerValidationState.Failure -> {
                    QRCodeGenerationErrorDialog(
                        viewModel.qrCodeGenerationState,
                        (viewModel.qrCodeGenerationState.value as ServerValidationState.Failure).message
                            ?: "Error generating QR code"
                    )
                }

                is ServerValidationState.Success -> {
                    QRCodeGeneratedDialog(
                        viewModel,
                        navController
                    )

                }
            }
            when (viewModel.openOrderValidationConfirmationDialog.value) {
                true -> {
                    OrderValidationConfirmationDialog(viewModel)
                }

                false -> {}
            }

        }
    }
}

@Composable
fun OrderValidationButton(openOrderValidationConfirmationDialog: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { openOrderValidationConfirmationDialog.value = true }) {
            Text(text = "Validate Order")
        }
    }
}


@Composable
fun OrderValidationConfirmationDialog(
    viewModel: OrderDetailsViewModel
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
        },
        title = {
            Text(text = "Validate order?", textAlign = TextAlign.Center)
        },
        text = { Text("Are you sure you want to validate this order? After doing so, you will be credited in you account!") },
        onDismissRequest = {

        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.openOrderValidationConfirmationDialog.value = false
                }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openOrderValidationConfirmationDialog.value = false
                    viewModel.validateOrder()
                }
            ) {
                Text("Confirm")
            }
        }
    )
}

@Composable
fun QRCodeGeneratedDialog(viewModel: OrderDetailsViewModel, navController: NavController) {
    Dialog(
        onDismissRequest = {
            viewModel.qrCodeGenerationState.value = null; navController.popBackStack()
        },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Order Completed",
                    tint = Color.Green,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "Present this code at the Cafeteria Terminal",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
                viewModel.qrCode.value?.let { BitmapPainter(it.asImageBitmap()) }?.let {
                    Image(
                        painter = it,
                        contentDescription = "Order Validation QR Code",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(250.dp)
                    )
                }


            }
        }
    }
}

@Composable
fun QRCodeGenerationErrorDialog(
    qrCodeGenerationState: MutableState<ServerValidationState?>,
    s: String
) {
    Dialog(
        onDismissRequest = { qrCodeGenerationState.value = null },
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
                    contentDescription = "Error generating QR code",
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
fun LoadingQRCodeDialog(s: String) {
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
fun OrderDetails(viewModel: OrderDetailsViewModel) {
    ProductsList(viewModel.order.value!!.orderProducts, viewModel)
    VouchersList(viewModel.order.value!!.vouchers)
}

@Composable
fun ProductsList(products: List<OrderProductWithProduct>, viewModel: OrderDetailsViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Products",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(products.size) { i ->
            ProductItem(products[i], viewModel)
        }
    }
}

@Composable
fun ProductItem(orderProduct: OrderProductWithProduct, viewModel: OrderDetailsViewModel) {
    var icon = Icons.Default.ChevronRight
    when {
        orderProduct.product.name.toString().contains("Coffee") -> icon = Icons.Default.Coffee
        orderProduct.product.name.toString().contains("Popcorn") -> icon = Icons.Default.Fastfood
        orderProduct.product.name.toString().contains("Soda") -> icon = Icons.Default.LocalDrink
        orderProduct.product.name.toString().contains("Sandwich") -> icon =
            Icons.Default.LunchDining
    }
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = md_theme_light_onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 15.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp, 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = "Product Icon")
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vouchers",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        if (vouchers.isEmpty()) Text(text = "No vouchers applied to this order.")
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



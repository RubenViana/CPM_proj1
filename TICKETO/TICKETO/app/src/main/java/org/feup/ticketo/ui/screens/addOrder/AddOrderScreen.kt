package org.feup.ticketo.ui.screens.addOrder

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    LaunchedEffect(viewModel) {
        Log.i("hello", "E")
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
                            ?: "Loading menu..."
                    )
                }

                is ServerValidationState.Failure -> {
                    EmptyMenu()
                }

                is ServerValidationState.Success -> {
                    Log.i("success", "succes")
                    if (viewModel.menu.value.isEmpty()) {
                        EmptyMenu()
                    } else {
                        MenuList(viewModel, navController)
                        when (viewModel.fetchVouchersFromStorageState.value) {
                            is ServerValidationState.Loading -> {
                                LoadingVouchers(
                                    (viewModel.fetchVouchersFromStorageState.value as ServerValidationState.Loading).message
                                        ?: "Loading vouchers..."
                                )
                            }

                            is ServerValidationState.Failure -> {
                                EmptyVouchersList()
                            }

                            is ServerValidationState.Success -> {
                                if (viewModel.menu.value.isEmpty()) {
                                    EmptyVouchersList()
                                } else {
                                    VouchersList(viewModel, navController)
                                }
                            }
                        }
                        TotalPriceAndCheckoutButton(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun TotalPriceAndCheckoutButton(viewModel: AddOrderViewModel) {
    Column {
        Row(
            Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Price",
                style = TextStyle(fontSize = 20.sp),
                fontWeight = FontWeight.Bold
            )
            Text(viewModel.total_price.value.toString() + " €")
        }

        Button(
            onClick = { viewModel.checkout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Order")
        }
    }
}

@Composable
fun VouchersList(viewModel: AddOrderViewModel, navController: NavHostController) {
    LazyColumn {
        items(viewModel.vouchers.value.size) { i ->
            VoucherItem(viewModel.vouchers.value[i], viewModel)
        }
    }
}

@Composable
fun VoucherItem(voucher: Voucher, viewModel: AddOrderViewModel) {
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
                checked = viewModel.orderVouchers.value.contains(voucher),
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
fun MenuList(viewModel: AddOrderViewModel, navController: NavHostController) {
    LazyColumn {
        items(viewModel.menu.value.size) { i ->
            Product(product = viewModel.menu.value[i], viewModel)
        }
    }
}

@Composable
fun Product(product: Product, viewModel: AddOrderViewModel) {
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

                    Text(text = viewModel.productQuantity(product))

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

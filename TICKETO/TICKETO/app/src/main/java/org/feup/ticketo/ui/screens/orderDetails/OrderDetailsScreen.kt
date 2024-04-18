package org.feup.ticketo.ui.screens.orderDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

@Composable
fun OrderDetailsScreen(navController: NavController, viewModel: OrderDetailsViewModel) {
    LaunchedEffect(viewModel) {
//        viewModel.fetchOrder()
    }
}
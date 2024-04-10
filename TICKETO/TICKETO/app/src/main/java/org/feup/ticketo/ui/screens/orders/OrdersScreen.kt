package org.feup.ticketo.ui.screens.orders

import android.view.MenuItem
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun OrdersScreen(navController: NavHostController, viewModel: OrdersViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "Cafeteria Menu", modifier = Modifier.padding(16.dp))

        // Display menu items
        LazyColumn {
            items(menuItems) { menuItem ->
                MenuItemCard(menuItem)
            }
        }

        // Add Voucher Button
       /* if (vouchers.isNotEmpty()) {
            Button(
                onClick = { /* Add voucher to the order */ },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Add Voucher")
            }
        }*/

        // Confirm Order Button
        Button(
            onClick = { /* Confirm the order */ },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Confirm Order")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(menuItem: MenuItem) {
    OutlinedCard(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.outlinedCardColors(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = menuItem.name)
            Text(text = "Price: ${menuItem.price}")
            Text(text = menuItem.description)
        }
    }
}
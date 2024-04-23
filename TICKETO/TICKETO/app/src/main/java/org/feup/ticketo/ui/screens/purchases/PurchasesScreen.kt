package org.feup.ticketo.ui.screens.purchases

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Cancel
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.volley.VolleyError
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.PurchaseWithTicketsAndEventsAndVouchers
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.data.storage.Voucher
import org.feup.ticketo.ui.screens.eventTickets.TicketShape
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary
import org.feup.ticketo.utils.getServerResponseErrorMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasesScreen(
    navController: NavController,
    viewModel: PurchasesViewModel
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = md_theme_light_background),
    ) {
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
                    Text("Purchases")
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
            // PAGE CONTENT
            LaunchedEffect(viewModel) {
                viewModel.fetchPurchases()
            }

            when (viewModel.fetchPurchasesFromServerState.value) {
                is ServerValidationState.Loading -> {
                    LoadingPurchases()
                }

                is ServerValidationState.Success -> {
                    Purchases(viewModel = viewModel)
                }

                is ServerValidationState.Failure -> {
                    LoadingPurchasesFailed(
                        (viewModel.fetchPurchasesFromServerState.value as ServerValidationState.Failure).error
                    )
                }
            }
        }
    }
}

@Composable
private fun Purchases(
    viewModel: PurchasesViewModel
) {
    LazyColumn {
        items(viewModel.purchases.value.size) { purchase ->
            PurchaseCard(purchase = viewModel.purchases.value[purchase], viewModel)
        }
    }
}

@Composable
fun PurchaseCard(purchase: PurchaseWithTicketsAndEventsAndVouchers, viewModel: PurchasesViewModel) {
    var expanded by remember { mutableStateOf(false) }

    val modifier = if (expanded) {
        Modifier
            .padding(10.dp)
    } else {
        Modifier
            .padding(10.dp)
            .height(100.dp)
    }

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = md_theme_light_onPrimary
        ),
        modifier = modifier.clickable { if (!expanded) expanded = !expanded },

        ) {
        Box {
            Box {
                purchase.event.picture?.let {
                    BitmapFactory.decodeByteArray(
                        purchase.event.picture,
                        0,
                        it.size
                    ).asImageBitmap()
                }
                    ?.let {
                        if (expanded) {
                            Image(
                                contentDescription = "Event picture",
                                bitmap = it,
                                modifier = Modifier
                                    .scale(10f)
                                    .fillMaxSize()
                                    .alpha(0.3f)
                                    .blur(8.dp),
                                contentScale = ContentScale.FillHeight,
                            )
                        } else {
                            Image(
                                contentDescription = "Event picture",
                                bitmap = it,
                                modifier = Modifier
                                    .scale(2f)
                                    .fillMaxWidth()
                                    .alpha(0.3f)
                                    .blur(8.dp),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
            }
            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.wrapContentHeight()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp, bottom = 15.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Event", fontWeight = FontWeight.Bold)
                            purchase.event.name?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Date", fontWeight = FontWeight.Bold)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = purchase.purchase.date.orEmpty().substring(0, 10),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = purchase.purchase.date.orEmpty().substring(10),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Total", fontWeight = FontWeight.Bold)
                            Text(
                                text = purchase.purchase.total_price.toString() + " €",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }
                    // Display tickets and vouchers when expanded
                    if (expanded) {
                        UserInfo(viewModel)
                        TicketsList(purchase)
                        VouchersList(purchase.vouchers)
                        //                    ReceiptButton()
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfo(viewModel: PurchasesViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Customer", fontWeight = FontWeight.Bold)
            Text(
                text = viewModel.customerName.value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Tax Number", fontWeight = FontWeight.Bold)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = viewModel.taxNumber.value,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun VouchersList(vouchers: List<Voucher>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vouchers",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        if (vouchers.isEmpty()) Text(text = "No vouchers associated with this purchase.")

        vouchers.forEach {
            VoucherItem(voucher = it)
        }
    }

}

@Composable
fun VoucherItem(voucher: Voucher) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = voucher.description.toString())
    }
}

@Composable
private fun TicketsList(purchase: PurchaseWithTicketsAndEventsAndVouchers) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tickets",
            style = TextStyle(fontSize = 20.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        purchase.tickets.forEach { ticket ->
            TicketCard(ticket, purchase.event)
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket, event: Event) {
    ElevatedCard(
        shape = TicketShape(circleRadius = 14.dp, cornerSize = CornerSize(8.dp)),
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
                Text(text = "Date", fontWeight = FontWeight.Bold)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = event.date.orEmpty().substring(0, 10),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = event.date.orEmpty().substring(10),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Seat", fontWeight = FontWeight.Bold)
                Text(text = ticket.place.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Price", fontWeight = FontWeight.Bold)
                Text(
                    text = event.price.toString() + " €",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Used", fontWeight = FontWeight.Bold)
                if (ticket.used == true) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FactCheck,
                        contentDescription = null
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingPurchases() {
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
fun LoadingPurchasesFailed(error: VolleyError?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Failed to load purchases")
        Text(getServerResponseErrorMessage(error).orEmpty())
    }
}
package org.feup.ticketo.ui.screens.eventTickets

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTicketsScreen(navController: NavHostController, viewModel: EventTicketsViewModel) {

    LaunchedEffect(viewModel) {
        viewModel.getEventTickets()
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
                    Text("${viewModel.eventTickets.value?.event?.name} Tickets")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
            Box()
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    viewModel.eventTickets.value?.event?.picture?.let {
                        BitmapFactory.decodeByteArray(
                            viewModel.eventTickets.value?.event?.picture,
                            0,
                            it.size
                        ).asImageBitmap()
                    }
                        ?.let {
                            Image(
                                contentDescription = "Event picture",
                                bitmap = it,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(0.8f)
                                    .blur(8.dp)
                            )
                        }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    )
                    {
                        LazyRow(
                            modifier = Modifier
                                .height(650.dp)
                                .width(350.dp)
                        ) {
                            viewModel.eventTickets.value?.tickets?.let {
                                items(it.size) { item ->
                                    viewModel.eventTickets.value?.event?.let { it1 ->
                                        QRCodeCard(
                                            it[item],
                                            it1,
                                            viewModel
                                        )
                                    }
                                }
                            }
                        }
                        Button(onClick = { viewModel.validateTickets() }) {
                            Text(text = "Validate Selected Tickets")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QRCodeCard(ticket: Ticket, event: Event, viewModel: EventTicketsViewModel) {
    OutlinedCard(
//        modifier = Modifier.padding(50.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = md_theme_light_onPrimary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
//            val qrcode = generateQRCode(ticket)
//            qrcode?.let { BitmapPainter(it.asImageBitmap()) }?.let {
//                Image(
//                    painter = it,
//                    contentDescription = null,
//                    modifier = Modifier.size(300.dp)
//                )
//            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row {
                        Text(text = "Ticket:  ")
                    }
                    Row {
                        Text(text = "Date:  ")
                    }
                    Row {
                        Text(text = "Seat:  ")
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Text(text = ticket.ticket_id.orEmpty(), fontWeight = FontWeight.Bold)
                    }
                    Row {
                        Text(text = event.date!!, fontWeight = FontWeight.Bold)
                    }
                    Row {
                        Text(text = ticket.place.orEmpty(), fontWeight = FontWeight.Bold)
                    }
                }
            }
            checkBox(ticket = ticket, viewModel = viewModel)
        }
    }
}

@Composable
fun checkBox(ticket: Ticket, viewModel: EventTicketsViewModel) {
    Checkbox(
        checked = viewModel.selectedTickets.value.contains(ticket),
        onCheckedChange = {
            viewModel.handleTicketOnCheckedChange(ticket)
        }
    )
}
package org.feup.ticketo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary
import org.feup.ticketo.utils.generateQRCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(navController: NavHostController, eventTickets: EventTickets) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = md_theme_light_background),
        horizontalAlignment = Alignment.CenterHorizontally
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
                Text("${eventTickets.eventName} Tickets")
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        )
        LazyRow {
            eventTickets.tickets?.let {
                items(it.size) { item ->
                    QRCodeCard(eventTickets.tickets[item], eventTickets.eventName.orEmpty(), eventTickets.eventDate.orEmpty())
                }
            }
        }

    }
}

@Composable
fun QRCodeCard(ticket: Ticket, eventName: String, eventDate: String) {
    OutlinedCard(
        modifier = Modifier.padding(50.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = md_theme_light_onPrimary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val qrcode = generateQRCode(ticket)
            qrcode?.let { BitmapPainter(it.asImageBitmap()) }?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ){
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
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Row {
                        Text(text = ticket.ticket_id.orEmpty(), fontWeight = FontWeight.Bold)
                    }
                    Row {
                        Text(text = eventDate, fontWeight = FontWeight.Bold)
                    }
                    Row {
                        Text(text = ticket.place.orEmpty(), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
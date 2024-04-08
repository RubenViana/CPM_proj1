package org.feup.ticketo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.feup.ticketo.data.Event
import org.feup.ticketo.data.Ticket
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary
import org.feup.ticketo.utils.generateQRCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(navController: NavHostController, ticket: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
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
                Text("Ticket $ticket")
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        )
        // get ticket object from ticketId passed above
        val t = Ticket(
            ticket_id = ticket,
            purchase_id = 1,
            event_id = 1,
            purchase_date = "2022-01-01",
            used = false,
            qrcode = "qrcode",
            place = "A1"
        )
        QRCodeCard(t)

    }
}

@Composable
fun QRCodeCard(ticket: Ticket) {
    OutlinedCard(
        modifier = Modifier.padding(50.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = md_theme_light_onPrimary
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val qrcode = generateQRCode(ticket.qrcode)
            qrcode?.let { BitmapPainter(it.asImageBitmap()) }?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            Text(text = "Event: ${ticket.event_id}")
            Text(text = "Date: {ticket.event_id.date}")
            Text(text = "Seat: ${ticket.place}")
        }
    }
}
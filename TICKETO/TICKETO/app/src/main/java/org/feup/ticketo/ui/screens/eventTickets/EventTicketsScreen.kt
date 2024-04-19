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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.rotationMatrix
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
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
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
                            .fillMaxSize()
                            .padding(10.dp)
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    )
                    {
                        LazyColumn(
                        ) {
                            viewModel.eventTickets.value?.tickets?.let {
                                items(it.size) { item ->
                                    viewModel.eventTickets.value?.event?.let { it1 ->
                                        TicketCard(
                                            ticket = it[item],
                                            event = it1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
                    Text(text = event.date.orEmpty().substring(0, 10), style = MaterialTheme.typography.bodyMedium)
                    Text(text = event.date.orEmpty().substring(10), style = MaterialTheme.typography.bodyMedium)
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
                Text(text = "QR Code", fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(imageVector = Icons.Default.QrCode2, contentDescription = null)
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

class TicketShape(
    private val circleRadius: Dp,
    private val cornerSize: CornerSize
) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(path = getPath(size, density))
    }

    private fun getPath(size: Size, density: Density): Path {
        val roundedRect = RoundRect(size.toRect(), CornerRadius(cornerSize.toPx(size, density)))
        val roundedRectPath = Path().apply { addRoundRect(roundedRect) }
        return Path.combine(operation = PathOperation.Intersect, path1 = roundedRectPath, path2 = getTicketPath(size, density))
    }

    private fun getTicketPath(size: Size, density: Density): Path {
        val middleX = size.width.div(other = 2)
        val middleY = size.height.div(other = 2)
        val circleRadiusInPx = with(density) { circleRadius.toPx() }
        return Path().apply {
            reset()
            // Ensure we start drawing line at top left
            lineTo(x = 0F, y = 0F)
            // Draw line to left middle
            lineTo(x = 0F, y = middleY)
            // Draw left cutout
            arcTo(
                rect = Rect(
                    left = 0F.minus(circleRadiusInPx),
                    top = middleY.minus(circleRadiusInPx),
                    right = circleRadiusInPx,
                    bottom = middleY.plus(circleRadiusInPx)
                ),
                startAngleDegrees = -90F,
                sweepAngleDegrees = 180F,
                forceMoveTo = false
            )
            // Draw line to bottom left
            lineTo(x = 0F, y = size.height)
            // Draw line to bottom right
            lineTo(x = size.width, y = size.height)

            // Draw line to right middle
            lineTo(x = size.width, y = middleY)
            // Draw right cutout
            arcTo(
                rect = Rect(
                    left = size.width.minus(circleRadiusInPx),
                    top = middleY.minus(circleRadiusInPx),
                    right = size.width.plus(circleRadiusInPx),
                    bottom = middleY.plus(circleRadiusInPx)
                ),
                startAngleDegrees = 90F,
                sweepAngleDegrees = 180F,
                forceMoveTo = false
            )
            // Draw line to top right
            lineTo(x = size.width, y = 0F)
            // Draw line back to top left
            lineTo(x = 0F, y = 0F)
        }
    }
}
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Event
import org.feup.ticketo.data.storage.Ticket
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTicketsScreen(navController: NavHostController, viewModel: EventTicketsViewModel) {

    LaunchedEffect(viewModel) {
        viewModel.fetchTicketsFromDatabaseState.value = ServerValidationState.Loading("Loading event tickets...")
        viewModel.getEventTickets()
    }

    when {
        viewModel.fetchTicketsFromDatabaseState.value is ServerValidationState.Loading -> {
            LoadingEventTickets()
        }
    }

    when {
        viewModel.fetchTicketsFromDatabaseState.value is ServerValidationState.Success -> {
            EventTickets(viewModel, navController)
        }
    }

    when {
        viewModel.qrCodeGenerationState.value is ServerValidationState.Loading -> {
            QRCodeGenerationLoadingDialog()
        }
    }

    when {
        viewModel.qrCodeGenerationState.value is ServerValidationState.Success -> {
            QRCodeGenerationSuccessfulDialog(viewModel.qrCodeGenerationState, viewModel, navController)
        }
    }

    when {
        viewModel.qrCodeGenerationState.value is ServerValidationState.Failure -> {
            QRCodeGenerationFailedDialog(viewModel)
        }
    }

    when {
        viewModel.openQRCodeGenerationConfirmationDialog.value -> {
            QRCodeGenerationConfirmationDialog(viewModel)
        }
    }
}

@Composable
fun LoadingEventTickets() {
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTickets(viewModel: EventTicketsViewModel, navController: NavHostController){
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
                    IconButton(onClick = {
                        viewModel.selectTicketsToQRCodeState.value = !viewModel.selectTicketsToQRCodeState.value
                        if (!viewModel.selectTicketsToQRCodeState.value) {
                            viewModel.selectedTickets.value = emptyList()
                        }
                    }
                    ) {
                        if (viewModel.selectTicketsToQRCodeState.value) {
                            Icon(imageVector = Icons.Outlined.Cancel, contentDescription = null)
                        } else {
                            Icon(imageVector = Icons.Default.SelectAll, contentDescription = null)
                        }
                    }
                    if (viewModel.selectTicketsToQRCodeState.value) {
                        IconButton(
                            onClick = {
                                if (viewModel.selectedTickets.value.isNotEmpty()) {
                                    viewModel.openQRCodeGenerationConfirmationDialog.value = true
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
                        }
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
                                            event = it1,
                                            viewModel
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
fun TicketCard(ticket: Ticket, event: Event, viewModel: EventTicketsViewModel) {
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
                if (!viewModel.selectTicketsToQRCodeState.value){
                    IconButton(
                        onClick = { viewModel.selectedTickets.value = listOf(ticket); viewModel.openQRCodeGenerationConfirmationDialog.value = true}
                    ) {
                        Icon(imageVector = Icons.Default.QrCode2, contentDescription = null)
                    }
                }
                else {
                    Checkbox(
                        checked = viewModel.selectedTickets.value.contains(ticket),
                        onCheckedChange = { viewModel.handleTicketOnCheckedChange(ticket) }
                    )
                }
            }
        }
    }
}

@Composable
fun QRCodeGenerationConfirmationDialog(
    viewModel: EventTicketsViewModel,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.QrCode2, contentDescription = null)
        },
        title = {
            Text(text = "QR Code generation?", textAlign = TextAlign.Center)
        },
        text = { Text("Are you sure you want to generate the QR code for this ticket(s)? After doing so, your ticket(s) will be set as used!") },
        onDismissRequest = {

        },
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.openQRCodeGenerationConfirmationDialog.value = false
                    viewModel.selectedTickets.value = emptyList()
                }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.openQRCodeGenerationConfirmationDialog.value = false
                    viewModel.selectTicketsToQRCodeState.value = false
                    viewModel.validateTickets()
                }
            ) {
                Text("Confirm")
            }
        }
    )
}

@Composable
fun QRCodeGenerationFailedDialog(
    viewModel: EventTicketsViewModel
) {
    Dialog(
        onDismissRequest = { viewModel.qrCodeGenerationState.value = null; viewModel.selectedTickets.value = emptyList()},
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
                    contentDescription = "QR Code Generation Failed",
                    tint = Color.Red,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "QR Code Generation Failed",
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
fun QRCodeGenerationSuccessfulDialog(
    qrCodeGenerationState: MutableState<ServerValidationState?>,
    viewModel: EventTicketsViewModel,
    navController: NavHostController
) {
    Dialog(
        onDismissRequest = { qrCodeGenerationState.value = null; navController.popBackStack() },
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "QR Code Generated Successfully",
                    tint = Color.Green,
                    modifier = Modifier.size(50.dp)
                )
                viewModel.qrCode.value?.let { BitmapPainter(it.asImageBitmap()) }?.let {
                    Image(
                        painter = it,
                        contentDescription = "Ticket Validation QR Code",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(250.dp)
                    )
                }
                Text(
                    text = "Present this code at the Ticket Terminal",
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
fun QRCodeGenerationLoadingDialog() {
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
            }
        }
    }
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
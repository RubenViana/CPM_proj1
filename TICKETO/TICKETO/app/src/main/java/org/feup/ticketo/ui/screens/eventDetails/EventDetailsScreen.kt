package org.feup.ticketo.ui.screens.eventDetails

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.android.volley.VolleyError
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary
import org.feup.ticketo.utils.getServerResponseErrorMessage

@Composable
fun EventDetailsScreen(navController: NavHostController, viewModel: EventDetailsViewModel) {

    LaunchedEffect(viewModel) {
        viewModel.fetchEventFromServerState.value =
            ServerValidationState.Loading("Loading event details...")
        viewModel.fetchEvent()
    }

    when {
        viewModel.fetchEventFromServerState.value is ServerValidationState.Success -> {
            EventDetails(viewModel, navController)
        }
    }
    when {
        viewModel.fetchEventFromServerState.value is ServerValidationState.Failure -> {
            LoadingEventDetailsFailedDialog(
                (viewModel.fetchEventFromServerState.value as ServerValidationState.Failure).error,
                viewModel
            )
        }
    }
    when {
        viewModel.fetchEventFromServerState.value is ServerValidationState.Loading -> {
            LoadingEventDetails(
                (viewModel.fetchEventFromServerState.value as ServerValidationState.Loading).message,
                navController
            )
        }
    }
    when {
        viewModel.purchaseTicketsInServerState.value is ServerValidationState.Success -> {
            PurchaseSuccessfulDialog(viewModel.purchaseTicketsInServerState)
        }
    }
    when {
        viewModel.purchaseTicketsInServerState.value is ServerValidationState.Failure -> {
            PurchaseFailedDialog(
                (viewModel.purchaseTicketsInServerState.value as ServerValidationState.Failure).error,
                viewModel.purchaseTicketsInServerState
            )
        }
    }
    when {
        viewModel.purchaseTicketsInServerState.value is ServerValidationState.Loading -> {
            LoadingPurchaseDialog()
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingEventDetails(message: String, navController: NavHostController) {
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
fun LoadingEventDetailsFailedDialog(error: VolleyError?, viewModel: EventDetailsViewModel) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Close, contentDescription = "Failed to load event details")
        },
        title = {
            Text(text = "Failed to load event details", textAlign = TextAlign.Center)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Something went wrong", textAlign = TextAlign.Center)
                Text(
                    text = getServerResponseErrorMessage(error).orEmpty(),
                    textAlign = TextAlign.Center
                )
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.fetchEvent()
                }
            ) {
                Text("Retry")
            }
        }
    )

}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EventDetails(
    viewModel: EventDetailsViewModel,
    navController: NavHostController,
    color_bg: Color = md_theme_light_background
) {
    Surface(
        Modifier
            .fillMaxSize(),
        color = color_bg
    ) {
        Column(
            Modifier
                .fillMaxSize(),
        ) {
            Box() {
                Box(modifier = Modifier.height(500.dp)) {
                    viewModel.event.picture?.let {
                        BitmapFactory.decodeByteArray(
                            viewModel.event.picture,
                            0,
                            it.size
                        ).asImageBitmap()
                    }
                        ?.let {
                            Image(
                                contentDescription = "Event picture",
                                bitmap = it,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        color_bg
                                    ),
                                    startY = 800f,
                                )
                            )
                    )
                }
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        actionIconContentColor = md_theme_light_onPrimary,
                        navigationIconContentColor = md_theme_light_onPrimary,
                        titleContentColor = md_theme_light_onPrimary,
                        scrolledContainerColor = Color.Transparent
                    ),
                    title = {
                        Text("")
                    },
                    modifier = Modifier.statusBarsPadding(),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 0.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = viewModel.event.name!!, style = TextStyle(fontSize = 40.sp))
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Date",
                            style = TextStyle(fontSize = 20.sp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(viewModel.event.date!!)
                    }
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
                        Text(viewModel.event.price.toString() + "€")
                    }
                    Column(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "Tickets",
                                style = TextStyle(fontSize = 20.sp),
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                Text(text = "${viewModel.numberTickets} ")
                                Icon(
                                    Icons.Default.ConfirmationNumber,
                                    contentDescription = null
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            IconButton(
                                onClick = { viewModel.decreaseTickets() },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = null
                                )
                            }
                            IconButton(
                                onClick = { viewModel.increaseTickets() },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                when {
                    viewModel.numberTickets > 0 -> {
                        Button(
                            onClick = { viewModel.openPurchaseConfirmationDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Buy")
                        }
                    }
                }
            }
        }
        when {
            viewModel.openPurchaseConfirmationDialog -> {
                PurchaseConfirmationDialog(
                    viewModel.numberTickets,
                    viewModel.event.name!!,
                    viewModel
                )
            }
        }
    }
}

@Composable
fun PurchaseConfirmationDialog(
    numberTickets: Int,
    eventName: String,
    viewModel: EventDetailsViewModel
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.ConfirmationNumber, contentDescription = null)
        },
        title = {
            Text(text = "Buy selected tickets?", textAlign = TextAlign.Center)
        },
        text = { Text("$numberTickets tickets for $eventName will be bought.") },
        onDismissRequest = {},
        dismissButton = {
            TextButton(
                onClick = {
                    viewModel.openPurchaseConfirmationDialog = false
                }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.checkout()
                }
            ) {
                Text("Confirm")
            }
        }
    )

}

@Composable
fun PurchaseFailedDialog(
    error: VolleyError?,
    purchaseTicketsInServerState: MutableState<ServerValidationState?>
) {
//    val errorMessage = getServerResponseErrorMessage(error)
//    if (errorMessage != null) {
//        Log.i("error", errorMessage)
//    }
    Dialog(
        onDismissRequest = { purchaseTicketsInServerState.value = null },
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
                    contentDescription = "Purchase Failed",
                    tint = Color.Red,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "Tickets Purchase Failed",
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
fun PurchaseSuccessfulDialog(purchaseTicketsInServerState: MutableState<ServerValidationState?>) {
    Dialog(
        onDismissRequest = { purchaseTicketsInServerState.value = null },
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
                    Icons.Default.CheckCircle,
                    contentDescription = "Purchase Completed",
                    tint = Color.Green,
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    text = "Tickets Purchase Successfully",
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
fun LoadingPurchaseDialog() {
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
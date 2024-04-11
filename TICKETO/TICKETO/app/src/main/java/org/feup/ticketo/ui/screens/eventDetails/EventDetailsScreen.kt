package org.feup.ticketo.ui.screens.eventDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.volley.VolleyError
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary
import org.feup.ticketo.utils.getServerResponseErrorMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(navController: NavHostController, viewModel: EventDetailsViewModel) {

    LaunchedEffect(viewModel) {
        viewModel.fetchEventFromServerState.value = ServerValidationState.Loading("Loading event details...")
        viewModel.fetchEvent()
    }

    EventDetails(viewModel, navController)

    PurchaseSuccessfulDialog(viewModel.puchaseTicketsInServerState)

    PurchaseFailedDialog(state.error, viewModel.openValidationDialog)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EventDetails(
    viewModel: EventDetailsViewModel,
    navController: NavHostController
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = md_theme_light_background),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                Text(viewModel.event.name.orEmpty())
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
        Image(
            painter = ColorPainter(color = Color.Cyan),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )
        Text(viewModel.event.name.orEmpty())
        Text(viewModel.event.date.orEmpty())
        Text(viewModel.event.price.toString())
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.decreaseTickets() },
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = null
                )
            }
            Text(viewModel.numberTickets.toString())
            IconButton(
                onClick = { viewModel.increaseTickets() },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
        Button(
            onClick = { viewModel.checkout() }
        ) {
            Text("Buy")
        }
    }
}

@Composable
fun PurchaseFailedDialog(error: VolleyError, openValidationDialog: MutableState<Boolean>) {
    val errorMessage = getServerResponseErrorMessage(error)
    AlertDialog(
        icon = {
            Icon(Icons.Default.Close, contentDescription = "Purchase Failed")
        },
        title = {
            Text(text = "Failed to buy tickets", textAlign = TextAlign.Center)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Something went wrong", textAlign = TextAlign.Center)
                if (errorMessage != null) {
                    Text(text = errorMessage, textAlign = TextAlign.Center)
                }
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    openValidationDialog.value = false
                }
            ) {
                Text("Confirm")
            }
        }
    )
}

@Composable
fun PurchaseSuccessfulDialog(openValidationDialog: MutableState<Boolean>) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.CheckCircle, contentDescription = "Purchase Completed")
        },
        title = {
            Text(text = "Tickets Purchase Successfully", textAlign = TextAlign.Center)
        },
        text = {},
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    openValidationDialog.value = false
                }
            ) {
                Text("Confirm")
            }
        }
    )
}
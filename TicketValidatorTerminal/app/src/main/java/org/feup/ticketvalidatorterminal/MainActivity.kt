package org.feup.ticketvalidatorterminal

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.feup.ticketvalidatorterminal.data.ServerValidationState
import org.feup.ticketvalidatorterminal.data.TicketValidationMessage
import org.feup.ticketvalidatorterminal.ui.theme.TicketValidatorTerminalTheme
import org.feup.ticketvalidatorterminal.utils.byteArrayToObject
import org.feup.ticketvalidatorterminal.utils.getServerResponseErrorMessage
import org.feup.ticketvalidatorterminal.utils.objectToJson
import org.feup.ticketvalidatorterminal.utils.serverUrl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicketValidatorTerminalApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketValidatorTerminalApp() {
    TicketValidatorTerminalTheme {

        val context = LocalContext.current
        // Variable to store the state of the validation from the server
        val serverValidationState = remember { mutableStateOf<ServerValidationState?>(null) }
        // Variable to toggle the opening of the validation dialog
        val openValidationDialog = remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .fillMaxWidth(),
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("Ticket Validator Terminal")
                        },
                    )
                }
            ) {
                Column(
                    Modifier
                        .padding(it)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Button
                    Button(
                        onClick = {
                            // Read QR Code
                            readQRCode(context, serverValidationState, openValidationDialog)
                        }) {
                        Text("Scan Tickets QR Code")
                    }
                }

                when {
                    openValidationDialog.value -> {
                        serverValidationState.value?.let { state ->
                            when (state) {
                                is ServerValidationState.Success -> ShowValidationSuccessfulDialog(
                                    openValidationDialog
                                )

                                is ServerValidationState.Failure -> ShowValidationFailedDialog(
                                    state.error,
                                    openValidationDialog
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun readQRCode(
    context: Context,
    serverValidationState: MutableState<ServerValidationState?>,
    openValidationDialog: MutableState<Boolean>
) {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    val scanner = GmsBarcodeScanning.getClient(context, options)

    scanner.startScan()
        .addOnSuccessListener { barcode ->
            val byteArray: ByteArray? = barcode.rawBytes
            if (byteArray != null) {
                val ticketsToValidate = byteArrayToObject<TicketValidationMessage>(byteArray)
                validateTicketsInServer(
                    context,
                    ticketsToValidate,
                    serverValidationState,
                    openValidationDialog
                )
            }
        }
}

fun validateTicketsInServer(
    context: Context,
    ticketsToValidate: TicketValidationMessage,
    serverValidationState: MutableState<ServerValidationState?>,
    openValidationDialog: MutableState<Boolean>
) {
    val url = serverUrl + "validate_tickets"
    val json = objectToJson(ticketsToValidate)
    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.POST, url, json,
        { response ->
            serverValidationState.value = ServerValidationState.Success(response)
            openValidationDialog.value = true
        },
        { error ->
            serverValidationState.value = ServerValidationState.Failure(error)
            openValidationDialog.value = true
        }
    )

    // Add the request to the RequestQueue
    Volley.newRequestQueue(context).add(jsonObjectRequest)
}


@Composable
fun ShowValidationSuccessfulDialog(openValidationDialog: MutableState<Boolean>) {
    ValidationSuccessfulDialog(openValidationDialog = openValidationDialog)
}

@Composable
fun ValidationSuccessfulDialog(openValidationDialog: MutableState<Boolean>) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.CheckCircle, contentDescription = "Tickets Validated")
        },
        title = {
            Text(text = "Tickets Validated Successfully", textAlign = TextAlign.Center)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "You are free to enter.", textAlign = TextAlign.Center)
                Text(text = "Enjoy the show!", textAlign = TextAlign.Center)
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
fun ShowValidationFailedDialog(
    error: VolleyError,
    openValidationDialog: MutableState<Boolean>
) {
    ValidationFailedDialog(error = error, openValidationDialog = openValidationDialog)
}

@Composable
fun ValidationFailedDialog(error: VolleyError, openValidationDialog: MutableState<Boolean>) {
    val errorMessage = getServerResponseErrorMessage(error)
    AlertDialog(
        icon = {
            Icon(Icons.Default.Close, contentDescription = "Validation Failed")
        },
        title = {
            Text(text = "Tickets Validation Failed", textAlign = TextAlign.Center)
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
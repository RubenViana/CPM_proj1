package org.feup.ticketvalidatorterminal

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.feup.ticketvalidatorterminal.data.TicketValidationMessage
import org.feup.ticketvalidatorterminal.ui.theme.TicketValidatorTerminalTheme
import org.json.JSONObject
import kotlin.concurrent.thread

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
        val validationState = remember { mutableStateOf<ValidationState?>(null) }
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
                            readQRCode(context, validationState, openValidationDialog)
                        }) {
                        Text("Scan Tickets QR Code")
                    }
                }

                when {
                    openValidationDialog.value -> {
                        validationState.value?.let { state ->
                            when (state) {
                                is ValidationState.Success -> ShowValidationSuccessfulDialog(
                                    openValidationDialog
                                )

                                is ValidationState.Failure -> ShowValidationFailedDialog(
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

sealed class ValidationState {
    data object Success : ValidationState()
    data class Failure(val error: VolleyError) : ValidationState()
}

fun readQRCode(
    context: Context,
    validationState: MutableState<ValidationState?>,
    openValidationDialog: MutableState<Boolean>
) {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    val scanner = GmsBarcodeScanning.getClient(context, options)

    scanner.startScan()
        .addOnSuccessListener { barcode ->
            val rawValue: ByteArray? = barcode.rawBytes
            if (rawValue != null) {
//                val tvm = TicketValidationMessage("hello", null, "aojdgnsorj")
//                val tvm_byte_array = Json.encodeToString(tvm).toByteArray()
//                Log.i("mytag_bytearray", tvm_byte_array.toString())
//                val ticketsToValidate = Json.decodeFromString<TicketValidationMessage>(tvm_byte_array.toString(Charsets.UTF_8))
//                Log.i("mytag_tvm_after", Json.encodeToString(ticketsToValidate))
                val ticketsToValidate = Json.decodeFromString<TicketValidationMessage>(rawValue.toString(Charsets.UTF_8))
                validateTicketsInServer(
                    context,
                    ticketsToValidate,
                    validationState,
                    openValidationDialog
                )
            }
        }
}

fun validateTicketsInServer(
    context: Context,
    ticketsToValidate: TicketValidationMessage,
    validationState: MutableState<ValidationState?>,
    openValidationDialog: MutableState<Boolean>
) {
    val url = "http://127.0.0.1:5000/validate_tickets"
    val json = JSONObject(Gson().toJson(ticketsToValidate))

    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.POST, url, json,
        { _ ->
            validationState.value = ValidationState.Success
            openValidationDialog.value = true
        },
        { error ->
            validationState.value = ValidationState.Failure(error)
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
    AlertDialog(
        icon = {
            Icon(Icons.Default.Close, contentDescription = "Validation Failed")
        },
        title = {
            Text(text = "Tickets Validation Failed", textAlign = TextAlign.Center)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Something went wrong", textAlign = TextAlign.Center)
                Text(text = error.message.toString(), textAlign = TextAlign.Center)
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



package org.feup.cafeteriaorderterminal

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
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.feup.cafeteriaorderterminal.data.ServerValidationState
import org.feup.cafeteriaorderterminal.ui.theme.CafeteriaOrderTerminalTheme
import org.feup.cafeteriaorderterminal.data.OrderValidationMessage
import org.feup.cafeteriaorderterminal.utils.*
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CafeteriaOrderTerminalApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeteriaOrderTerminalApp() {
    CafeteriaOrderTerminalTheme {

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
                            Text("Cafeteria Order Terminal")
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
                        Text("Scan Orders QR Code") // Show
                    }
                }

                when {
                    openValidationDialog.value -> {
                        serverValidationState.value?.let { state ->
                            when (state) {
                                is ServerValidationState.Success -> {
                                    val response = state.response // Access the response object
                                    ShowValidationSuccessfulDialog(openValidationDialog,response)
                                }

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
                val ordersToValidate = byteArrayToObject<OrderValidationMessage>(byteArray)
                validateOrdersInServer(
                    context,
                    ordersToValidate,
                    serverValidationState,
                    openValidationDialog
                )
            }
        }
}

fun validateOrdersInServer(
    context: Context,
    ordersToValidate: OrderValidationMessage,
    serverValidationState: MutableState<ServerValidationState?>,
    openValidationDialog: MutableState<Boolean>
) {
    val url = serverUrl + "validate_order"
    val json = objectToJson(ordersToValidate)
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
fun ShowValidationSuccessfulDialog(
    openValidationDialog: MutableState<Boolean>,
    response: JSONObject
) {
    ValidationSuccessfulDialog(
        openValidationDialog = openValidationDialog,
        response = response
    )
}

@Composable
fun ValidationSuccessfulDialog(openValidationDialog: MutableState<Boolean>,response: JSONObject) {

        val orderNumber = response.getString("order_id")
        val products = response.getJSONArray("products")
        val vouchers = response.getJSONArray("vouchers")
        val totalPrice = response.getDouble("total_price")

        AlertDialog(
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = "Orders Validated")
            },
            title = {
                Text(text = "Orders Validated Successfully", textAlign = TextAlign.Center)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display order number
                    Text("Order Number: $orderNumber")

                    // Display products
                    Text("Products Ordered:")
                    for (i in 0 until products.length()) {
                        val product = products.getJSONObject(i)
                        Text("- ${product.getString("product_id")}: ${product.getInt("quantity")}")
                    }

                    // Display vouchers accepted
                    Text("Vouchers Accepted:")
                    for (i in 0 until vouchers.length()) {
                        val voucher = vouchers.getJSONObject(i)
                        val accepted = voucher.getBoolean("accepted")
                        val voucherId = voucher.getString("voucher_id")
                        if (accepted) {
                            Text("- $voucherId")
                        }
                    }

                    // Display final price
                    Text("Total Price to Pay: $totalPrice")
                }
            },
            onDismissRequest = {
                openValidationDialog.value = false
            },
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
            Text(text = "Order Validation Failed", textAlign = TextAlign.Center)
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
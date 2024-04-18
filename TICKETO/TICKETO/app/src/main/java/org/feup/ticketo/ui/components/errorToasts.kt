package org.feup.ticketo.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.android.volley.VolleyError
import org.feup.ticketo.utils.getServerResponseErrorMessage

@Composable
fun serverErrorToast(start: String, error: VolleyError?) {
    val errorMessage = getServerResponseErrorMessage(error) ?: "Unknown Error"
    Toast.makeText(LocalContext.current, "$start: $errorMessage", Toast.LENGTH_LONG).show()
}

@Composable
fun errorToast(start: String, error: String) {
    Toast.makeText(LocalContext.current, "$start: $error", Toast.LENGTH_LONG).show()
}

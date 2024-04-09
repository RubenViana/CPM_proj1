package org.feup.ticketo.ui.screens.register

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.UserRegistrationMessage
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary
import org.feup.ticketo.utils.objectToJson
import org.feup.ticketo.utils.serverUrl

@Composable
fun RegisterScreen(navController: NavHostController, viewModel: RegisterViewModel, snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    Surface(
        color = md_theme_light_onPrimary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign Up",
                style = TextStyle(fontSize = 32.sp, fontFamily = FontFamily.Default),
                color = md_theme_light_primary
            )

            Spacer(modifier = Modifier.height(80.dp))

            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = md_theme_light_primary,
                    unfocusedBorderColor = md_theme_light_primary,
                    unfocusedLabelColor = md_theme_light_primary,
                    unfocusedLeadingIconColor = md_theme_light_primary,
                    focusedBorderColor = md_theme_light_primary,
                    focusedLabelColor = md_theme_light_primary,
                    focusedLeadingIconColor = md_theme_light_primary,
                    focusedTextColor = md_theme_light_primary,
                    cursorColor = md_theme_light_primary,
                    errorCursorColor = md_theme_light_primary,
                ),
                singleLine = true,
                label = { Text(text = "Username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PermIdentity,
                        contentDescription = null
                    )
                },
                value = viewModel.username.value,
                onValueChange = { viewModel.username.value = it },
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = md_theme_light_primary,
                    unfocusedBorderColor = md_theme_light_primary,
                    unfocusedLabelColor = md_theme_light_primary,
                    unfocusedLeadingIconColor = md_theme_light_primary,
                    focusedBorderColor = md_theme_light_primary,
                    focusedLabelColor = md_theme_light_primary,
                    focusedLeadingIconColor = md_theme_light_primary,
                    focusedTextColor = md_theme_light_primary,
                    cursorColor = md_theme_light_primary,
                    errorCursorColor = md_theme_light_primary,
                ),
                singleLine = true,
                label = { Text(text = "NIF") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PermIdentity,
                        contentDescription = null
                    )
                },
                value = viewModel.nif.value,
                onValueChange = { viewModel.nif.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = md_theme_light_primary,
                    unfocusedBorderColor = md_theme_light_primary,
                    unfocusedLabelColor = md_theme_light_primary,
                    unfocusedLeadingIconColor = md_theme_light_primary,
                    focusedBorderColor = md_theme_light_primary,
                    focusedLabelColor = md_theme_light_primary,
                    focusedLeadingIconColor = md_theme_light_primary,
                    focusedTextColor = md_theme_light_primary,
                    cursorColor = md_theme_light_primary,
                    errorCursorColor = md_theme_light_primary,
                ),
                singleLine = true,
                label = { Text(text = "Credit Card Number") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null
                    )
                },
                value = viewModel.creditCardNumber.value,
                onValueChange = { viewModel.creditCardNumber.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.padding(46.dp, 0.dp, 46.dp, 0.dp),

                )
            {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = md_theme_light_primary,
                        unfocusedBorderColor = md_theme_light_primary,
                        unfocusedLabelColor = md_theme_light_primary,
                        unfocusedLeadingIconColor = md_theme_light_primary,
                        focusedBorderColor = md_theme_light_primary,
                        focusedLabelColor = md_theme_light_primary,
                        focusedLeadingIconColor = md_theme_light_primary,
                        focusedTextColor = md_theme_light_primary,
                        cursorColor = md_theme_light_primary,
                        errorCursorColor = md_theme_light_primary,
                    ),
                    singleLine = true,
                    label = { Text(text = "Date") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null
                        )
                    },
                    value = viewModel.creditCardDate.value,
                    onValueChange = { viewModel.creditCardDate.value = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = md_theme_light_primary,
                        unfocusedBorderColor = md_theme_light_primary,
                        unfocusedLabelColor = md_theme_light_primary,
                        unfocusedLeadingIconColor = md_theme_light_primary,
                        focusedBorderColor = md_theme_light_primary,
                        focusedLabelColor = md_theme_light_primary,
                        focusedLeadingIconColor = md_theme_light_primary,
                        focusedTextColor = md_theme_light_primary,
                        cursorColor = md_theme_light_primary,
                        errorCursorColor = md_theme_light_primary,
                    ),
                    singleLine = true,
                    label = { Text(text = "Type") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null
                        )
                    },
                    value = viewModel.creditCardType.value,
                    onValueChange = { viewModel.creditCardType.value = it },
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        if (viewModel.username.value.isEmpty() || viewModel.nif.value.isEmpty() || viewModel.creditCardNumber.value.isEmpty() || viewModel.creditCardDate.value.isEmpty() || viewModel.creditCardType.value.isEmpty()){
                            scope.launch {
                                snackbarHostState.showSnackbar(message = "Missing Input Fields", duration = SnackbarDuration.Short)
                            }
                        }
                        else {
                            // handle user registration
                            navController.navigate("home") {
                                popUpTo(0)
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Sign Up")
                }
            }
        }
    }
}

fun registerUserInServer(context: Context, userRegistrationMessage: UserRegistrationMessage) {

    // Server endpoint
    val endpoint = "register_user"

    // Create the request body
    val json = objectToJson(userRegistrationMessage)

    // Create the request
    val request = JsonObjectRequest(
        Request.Method.POST, serverUrl + endpoint, json,
        { response ->
            // Handle response
        },
        { error ->

        }
    )

    // Add the request to the RequestQueue
    Volley.newRequestQueue(context).add(request)

}
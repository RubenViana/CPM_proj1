package org.feup.ticketo.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.feup.ticketo.data.UserRegistrationMessage
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary
import org.feup.ticketo.utils.objectToJson
import org.feup.ticketo.utils.serverUrl

@Composable
fun RegisterScreen(navController: NavHostController) {
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

            val username = remember { mutableStateOf(TextFieldValue()) }
            val password = remember { mutableStateOf(TextFieldValue()) }

            Text(
                text = "Sign Up",
                style = TextStyle(fontSize = 32.sp, fontFamily = FontFamily.Default),
                color = md_theme_light_primary
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
                label = { Text(text = "Username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PermIdentity,
                        contentDescription = null
                    )
                },
                value = username.value,
                onValueChange = { username.value = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
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
                label = { Text(text = "Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Password,
                        contentDescription = null
                    )
                },
                value = password.value,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password.value = it })

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo(0)
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
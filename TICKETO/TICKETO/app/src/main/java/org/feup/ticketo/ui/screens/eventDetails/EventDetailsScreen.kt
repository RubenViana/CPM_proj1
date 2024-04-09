package org.feup.ticketo.ui.screens.eventDetails

import android.inputmethodservice.Keyboard
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
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(navController: NavHostController, viewModel: EventDetailsViewModel) {
    Column (
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
        ){
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
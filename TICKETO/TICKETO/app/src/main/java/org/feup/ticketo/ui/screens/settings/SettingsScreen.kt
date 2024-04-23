package org.feup.ticketo.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.feup.ticketo.NavRoutes
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    LaunchedEffect(viewModel) {
        viewModel.getUserInfoFromDatabase()
    }
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
                Text("Settings")
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewModel.userInfo.value?.username?.let { Text(it) }
            viewModel.userInfo.value?.tax_number?.let { Text(it.toString()) }
            viewModel.cardInfo.value?.number?.let { Text(it) }
            viewModel.cardInfo.value?.validity?.let { Text(it) }
            viewModel.cardInfo.value?.type?.let { Text(it) }
        }
        // List of Settings Options
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = md_theme_light_onPrimary
            ),
            headlineContent = { viewModel.userInfo.value?.username?.let { Text(it) } },
            supportingContent = { Text("username") },
            leadingContent = {
                Icon(Icons.Default.AccountCircle, null)
            },
            trailingContent = {
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowRight, null)
                }
            }
        )
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = md_theme_light_onPrimary
            ),
            headlineContent = { Text("Past Purchases") },
            supportingContent = { Text("Consult all tickets you've bought") },
            leadingContent = {
                Icon(Icons.Default.AccountCircle, null)
            },
            trailingContent = {
                IconButton(
                    onClick = { navController.navigate(NavRoutes.Purchases.route) }
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowRight, null)
                }
            },
            modifier = Modifier.clickable { navController.navigate(NavRoutes.Purchases.route) }
        )
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = md_theme_light_onPrimary
            ),
            headlineContent = { Text("Past Orders") },
            supportingContent = { Text("Consult all orders you've validated") },
            leadingContent = {
                Icon(Icons.Default.AccountCircle, null)
            },
            trailingContent = {
                IconButton(
                    onClick = { navController.navigate(NavRoutes.PastOrders.route) }
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowRight, null)
                }
            },
            modifier = Modifier.clickable { navController.navigate(NavRoutes.PastOrders.route) }
        )
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = md_theme_light_onPrimary
            ),
            headlineContent = { Text("Profile") },
            supportingContent = { Text("Edit profile information") },
            leadingContent = {
                Icon(Icons.Default.AccountCircle, null)
            },
            trailingContent = {
                var checked2 by remember { mutableStateOf(true) }
                Switch(
                    checked = checked2,
                    onCheckedChange = { checked2 = it },
                    thumbContent = if (checked2) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                null,
                                modifier = Modifier.size(SwitchDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        )
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = md_theme_light_onPrimary
            ),
            headlineContent = { Text("Profile") },
            supportingContent = { Text("Edit profile information") },
            leadingContent = {
                Icon(Icons.Default.AccountCircle, null)
            },
            trailingContent = {
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(Icons.AutoMirrored.Default.ArrowRight, null)
                }
            }
        )


    }

}
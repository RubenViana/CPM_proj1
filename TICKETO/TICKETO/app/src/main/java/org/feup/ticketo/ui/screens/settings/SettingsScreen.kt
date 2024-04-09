package org.feup.ticketo.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    Column (
        Modifier.fillMaxSize().background(color = md_theme_light_background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
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
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
        )
        
        // List of Settings Options
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
                ){
                    Icon(Icons.AutoMirrored.Default.ArrowRight, null)
                }
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
                ){
                    Icon(Icons.AutoMirrored.Default.ArrowRight, null)
                }
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
                var checked1 by remember { mutableStateOf(true) }
                Switch(
                    checked = checked1,
                    onCheckedChange = {checked1 = it},
                    thumbContent = if (checked1) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                null,
                                modifier = Modifier.size(SwitchDefaults.IconSize)
                            )
                        }
                        } else{
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
                var checked2 by remember { mutableStateOf(true) }
                Switch(
                    checked = checked2,
                    onCheckedChange = {checked2 = it},
                    thumbContent = if (checked2) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                null,
                                modifier = Modifier.size(SwitchDefaults.IconSize)
                            )
                        }
                    } else{
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
                ){
                    Icon(Icons.AutoMirrored.Default.ArrowRight, null)
                }
            }
        )


    }

}
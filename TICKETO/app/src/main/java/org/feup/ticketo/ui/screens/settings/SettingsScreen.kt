package org.feup.ticketo.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.feup.ticketo.NavRoutes
import org.feup.ticketo.ui.theme.md_theme_light_background
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    LaunchedEffect(viewModel) {
        viewModel.getUserInfoFromDatabase()
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = md_theme_light_background),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(color = md_theme_light_background),
            horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        tint = md_theme_light_primary
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            viewModel.userInfo.value?.username.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            viewModel.userInfo.value?.tax_number.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    CreditCardCard(
                        number = viewModel.cardInfo.value?.number.toString(),
                        date = viewModel.cardInfo.value?.validity.toString(),
                        type = viewModel.cardInfo.value?.type.toString()
                    )
                }


                Spacer(modifier = Modifier.height(20.dp))

                // List of Settings Options
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = md_theme_light_onPrimary
                    ),
                    headlineContent = { Text("Past Purchases") },
                    supportingContent = { Text("Consult all tickets you've bought") },
                    leadingContent = {
                        Icon(Icons.Default.History, null)
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
                        Icon(Icons.Default.Fastfood, null)
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
                    headlineContent = { Text("Biometric Authentication") },
                    supportingContent = { Text("Feature to be implemented!") },
                    leadingContent = {
                        Icon(Icons.Default.Fingerprint, null)
                    },
                    trailingContent = {
                        var checked by remember { mutableStateOf(false) }
                        Switch(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            thumbContent = if (checked) {
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
            }
        }
    }
}

@Composable
fun CreditCardCard(
    number: String,
    date: String,
    type: String
){
    ElevatedCard(
        modifier = Modifier.size(150.dp,70.dp)
    ) {
        Box(){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Yellow,
                                Color.Red
                            )
                        )
                    )
            ) {

            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(number, style = MaterialTheme.typography.bodyLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(date, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            type.uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
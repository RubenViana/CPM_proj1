package org.feup.ticketo.ui.components

import android.content.res.Resources.Theme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.feup.ticketo.ui.theme.md_theme_light_onPrimary
import org.feup.ticketo.ui.theme.md_theme_light_primary

@ExperimentalMaterial3Api
@Composable
fun TopNavBar (
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    var title = ""
    if (currentDestination?.route != null) {
        title = currentDestination.route!!
    }

    if (title == "home"){
        LargeTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = md_theme_light_primary,
                actionIconContentColor = md_theme_light_onPrimary,
                navigationIconContentColor = md_theme_light_onPrimary,
                titleContentColor = md_theme_light_onPrimary,
                scrolledContainerColor = md_theme_light_primary
            ),
            title = {
                Text(
                    "TICKETO",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )
            },
            actions = {
                IconButton(onClick = { navController.navigate("Settings") }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
    else if (title == "tickets"){
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = md_theme_light_primary,
                actionIconContentColor = md_theme_light_onPrimary,
                navigationIconContentColor = md_theme_light_onPrimary,
                titleContentColor = md_theme_light_onPrimary,
                scrolledContainerColor = md_theme_light_primary
            ),
            title = {
                Text("Tickets"
                )
            },
            actions = {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            }


        )
    }
    else if (title == "orders"){
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = md_theme_light_primary,
                actionIconContentColor = md_theme_light_onPrimary,
                navigationIconContentColor = md_theme_light_onPrimary,
                titleContentColor = md_theme_light_onPrimary,
                scrolledContainerColor = md_theme_light_primary
            ),
            title = {
                Text("Orders")
            },
            actions = {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        )
    }
}
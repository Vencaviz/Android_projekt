package com.projekt.xvizvary.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.projekt.xvizvary.R

@Composable
fun SmartBudgetBottomBar(
    currentRoute: String?,
    onNavigate: (route: String) -> Unit
) {
    val items = listOf(
        BottomNavItem(Destination.Home.route, R.string.nav_home, Icons.Default.Home),
        BottomNavItem(Destination.Search.route, R.string.nav_search, Icons.Default.Search),
        BottomNavItem(Destination.Limits.route, R.string.nav_limits, Icons.Default.StackedBarChart),
        BottomNavItem(Destination.Profile.route, R.string.nav_profile, Icons.Default.Person)
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { androidx.compose.material3.Icon(item.icon, contentDescription = null) },
                label = { Text(text = stringResource(item.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)


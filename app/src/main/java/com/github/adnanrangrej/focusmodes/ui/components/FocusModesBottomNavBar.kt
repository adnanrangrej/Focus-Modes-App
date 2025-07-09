package com.github.adnanrangrej.focusmodes.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.MainScreenTab
import com.github.adnanrangrej.focusmodes.ui.theme.FocusTheme

@Composable
fun FocusModesBottomNavBar(
    modifier: Modifier = Modifier,
    navItems: List<MainScreenTab>,
    selectedItem: MainScreenTab,
    onNavItemClick: (MainScreenTab) -> Unit
) {

    NavigationBar(
        modifier = modifier,
        tonalElevation = FocusTheme.elevation.small
    ) {
        navItems.forEach { item ->
            val isSelected = selectedItem == item
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavItemClick(item) },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
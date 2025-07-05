package com.github.adnanrangrej.focusmodes.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.MainScreenTab

@Composable
fun FocusModesBottomNavBar(
    navItems: List<MainScreenTab>,
    selectedItem: MainScreenTab,
    onNavItemClick: (MainScreenTab) -> Unit
) {

    NavigationBar {
        navItems.forEach { item ->

            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onNavItemClick(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}
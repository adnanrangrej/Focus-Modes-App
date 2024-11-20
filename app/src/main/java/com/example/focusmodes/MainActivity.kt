package com.example.focusmodes

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.focusmodes.navigation.NavGraph
import com.example.focusmodes.navigation.Screen
import com.example.focusmodes.ui.theme.FocusModesTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusModesTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Outlined.Timer, contentDescription = "Pomodoro") },
                                label = { Text("Pomodoro") },
                                selected = currentRoute == Screen.Pomodoro.route,
                                onClick = { navController.navigate(Screen.Pomodoro.route) }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.AutoMirrored.Outlined.List, contentDescription = "Focus Modes") },
                                label = { Text("Focus Modes") },
                                selected = currentRoute == Screen.FocusModes.route,
                                onClick = { navController.navigate(Screen.FocusModes.route) }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Outlined.Assessment, contentDescription = "Progress") },
                                label = { Text("Progress") },
                                selected = currentRoute == Screen.Progress.route,
                                onClick = { navController.navigate(Screen.Progress.route) }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
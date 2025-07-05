package com.github.adnanrangrej.focusmodes.ui.navigation.graphs.userstats

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import com.github.adnanrangrej.focusmodes.ui.navigation.destination.userstats.UserStatsScreen
import com.github.adnanrangrej.focusmodes.ui.screens.userstats.UserStatsScreen
import com.github.adnanrangrej.focusmodes.ui.screens.userstats.UserStatsViewModel

fun EntryProviderBuilder<Any>.userStatsGraph() {

    entry<UserStatsScreen.UserStatsMain> {

        val viewModel: UserStatsViewModel = hiltViewModel()
        UserStatsScreen(viewModel = viewModel)
    }
}
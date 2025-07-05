package com.github.adnanrangrej.focusmodes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.adnanrangrej.focusmodes.domain.usecase.modes.SetFocusModeActiveUseCase
import com.github.adnanrangrej.focusmodes.ui.theme.FocusModesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var setFocusModeActiveUseCase: SetFocusModeActiveUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusModesTheme {
                FocusModesApp()
            }
        }
    }

    override fun onDestroy() {
        setFocusModeActiveUseCase(false, null)
        super.onDestroy()
    }
}
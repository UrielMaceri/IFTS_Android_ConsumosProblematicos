package com.example.ifts_android_consumosproblematicos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ifts_android_consumosproblematicos.ui.navigation.AppNavigation
import com.example.ifts_android_consumosproblematicos.ui.theme.TodoBienTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoBienTheme {
                AppNavigation()
            }
        }
    }
}

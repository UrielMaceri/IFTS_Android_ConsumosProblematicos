package com.example.ifts_android_consumosproblematicos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

//Reemplazar con objeto tema final
//import com.example.ifts_android_consumosproblematicos.ui.navigation.{Nav Obj}
//import com.example.ifts_android_consumosproblematicos.ui.theme.{Theme Obj}

// Borrar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //TodoBienTheme {   //Reemplazar con objeto tema final
            //    AppNavigation()
            //}
            MaterialTheme {
                Text("Prueba")
            }
        }
    }
}
package com.example.doanltdd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.doanltdd.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Gọi AppNavGraph thay vì gọi trực tiếp LoginScreen
            AppNavGraph()
        }
    }
}
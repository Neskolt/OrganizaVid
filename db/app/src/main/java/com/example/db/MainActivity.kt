package com.example.db

import AppNavGraph
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.db.ui.theme.DbTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear la instancia de HabitDatabase
        val db = HabitDatabase.getDatabase(applicationContext)
        val userDao = db.userDao() // Obtén el UserDao

        setContent {
            DbTheme {
                // Usamos el NavController aquí
                val navController = rememberNavController()

                // Configuramos el gráfico de navegación
                AppNavGraph(navController = navController, userDao = userDao)
            }
        }
    }
}
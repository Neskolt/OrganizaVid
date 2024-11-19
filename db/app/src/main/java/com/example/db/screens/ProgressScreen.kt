package com.example.db.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.db.entities.HabitEntity
import com.example.db.ui.theme.CajaProgreso


@Composable
fun ProgressScreen(habito: HabitEntity, habitId: Int, context: Context) {
    // Configuración de colores para los TextField
    val textFieldColors = TextFieldDefaults.colors(
        disabledTextColor = Color.Black,
        disabledContainerColor = Color.White,
        disabledLabelColor = Color.Gray
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center // Centramos el contenido dentro de Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
        ) {
            TextField(
                value = habito.name,
                onValueChange = {},
                label = { Text(text = "Nombre del Hábito") },
                enabled = false,
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = habito.description,
                onValueChange = {},
                label = { Text(text = "Motivo") },
                enabled = false,
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))



            Spacer(modifier = Modifier.height(16.dp))

            // Llamar a CajaProgreso y pasar habitId y context
            CajaProgreso(context = context, habitId = habitId)
        }
    }
}

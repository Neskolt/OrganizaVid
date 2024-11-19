package com.example.db.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.db.dao.UserDao
import com.example.db.entities.UserEntity
import com.example.db.viewModel.UserViewModel
import com.example.db.viewModel.UserViewModelFactory

@Composable
fun LoginScreen(navController: NavController, userDao: UserDao) {
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userDao)
    )

    val currentUser by userViewModel.currentUser.collectAsState(initial = null)

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val category = getCategoryByAge(user.age)
            navController.navigate("habit_screen/$category") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }

    var age by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    if (currentUser == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Iniciar sesión", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Edad") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (age.isNotEmpty() && age.toIntOrNull() != null) { // Verifica si la edad es válida
                        val ageInt = age.toInt() // Convierte la edad ingresada a un valor entero

                        // Validar que la edad sea mayor o igual a 10
                        if (ageInt >= 10) {
                            val user = UserEntity(age = ageInt)

                            // Llama a insertUser en UserViewModel
                            userViewModel.insertUser(user)

                            // Navega a la siguiente pantalla
                            val category = getCategoryByAge(ageInt)
                            navController.navigate("habit_screen/$category") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        } else {
                            // Si la edad es menor a 10, muestra un mensaje de error
                            errorMessage = "La edad debe ser mayor o igual a 10 para poder continuar."
                        }
                    } else {
                        errorMessage = "Por favor ingresa una edad válida." // Si la edad no es válida, muestra el mensaje de error
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red)
            }
        }
    }
}


fun getCategoryByAge(age: Int): String {
    return when {
        age < 18 -> "young"
        age in 18..60 -> "adult"
        else -> "old"
    }
}



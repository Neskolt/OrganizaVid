package com.example.db.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.db.entities.HabitEntity
import com.example.db.viewModel.HabitViewModel

@Composable
fun HabitScreen(ageGroup: String, navController: NavHostController, viewModel: HabitViewModel = viewModel()) {
    // Obtener los hábitos según el grupo de edad
    val habits by viewModel.habits.observeAsState(emptyList())

    // Variable para evitar la recarga de hábitos innecesaria
    var initialized by remember { mutableStateOf(false) }

    // Variables para el nombre y descripción del hábito
    var habitName by remember { mutableStateOf("") }
    var habitDescription by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) } // Para el diálogo de confirmación de eliminación
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) } // Guardar el hábito a eliminar
    var showEmptyFieldsDialog by remember { mutableStateOf(false) } // Para el diálogo de campos vacíos

    var isEditing by remember { mutableStateOf(false) }

    // Hábito que se está editando
    var habitToEdit by remember { mutableStateOf<HabitEntity?>(null) }

    // Solo se llama a getHabitsByAgeGroup si no se ha inicializado previamente
    LaunchedEffect(ageGroup) {
        if (!initialized) {
            viewModel.initializeDefaultHabitsIfNeeded(ageGroup) // Cambiamos a esta nueva función
            initialized = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gestión de Hábitos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de entrada para el nombre del hábito
        TextField(
            value = habitName,
            onValueChange = { habitName = it },
            label = { Text("Nombre del hábito") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de entrada para la descripción del hábito
        TextField(
            value = habitDescription,
            onValueChange = { habitDescription = it },
            label = { Text("Descripción del hábito") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para agregar un hábito
        Button(
            onClick = {
                val trimmedName = habitName.trim()
                val trimmedDescription = habitDescription.trim()

                if (trimmedName.isNotEmpty() && trimmedDescription.isNotEmpty()) {
                    if (isEditing && habitToEdit != null) {
                        // Actualizar el hábito existente
                        val updatedHabit = habitToEdit!!.copy(
                            name = trimmedName,
                            description = trimmedDescription
                        )
                        viewModel.updateHabit(updatedHabit)

                        // Salir del modo de edición
                        isEditing = false
                        habitToEdit = null
                    } else {
                        // Agregar un nuevo hábito
                        val newHabit = HabitEntity(
                            name = trimmedName,
                            description = trimmedDescription,
                            ageGroup = ageGroup
                        )
                        viewModel.addHabit(newHabit)
                    }

                    // Limpiar los campos de entrada
                    habitName = ""
                    habitDescription = ""
                } else {
                    // Mostrar alerta de campos vacíos o inválidos
                    showEmptyFieldsDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Actualizar hábito" else "Agregar hábito")
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar la lista de hábitos
        LazyColumn {
            items(habits) { habit ->
                HabitItem(
                    habit = habit,
                    navController = navController,
                    onEditClick = { updatedHabit ->
                        // Configurar el modo de edición
                        habitName = updatedHabit.name // Llena el campo con el nombre
                        habitDescription = updatedHabit.description // Llena el campo con la descripción
                        isEditing = true // Activa el modo de edición
                        habitToEdit = updatedHabit // Guarda la referencia al hábito actual
                    },
                    onDeleteClick = { habitToDelete = habit; showDialog = true }
                )
            }
        }


        // Mostrar AlertDialog de confirmación de eliminación
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar este hábito?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            habitToDelete?.let { viewModel.deleteHabit(it) }
                            showDialog = false // Cerrar el diálogo
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Mostrar AlertDialog de campos vacíos
        if (showEmptyFieldsDialog) {
            AlertDialog(
                onDismissRequest = { showEmptyFieldsDialog = false },
                title = { Text("Error") },
                text = { Text("El nombre y la descripción del hábito no pueden estar vacíos o contener solo espacios.") },
                confirmButton = {
                    TextButton(onClick = { showEmptyFieldsDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }
    }
}


@Composable
fun HabitItem(
    habit: HabitEntity,
    navController: NavHostController,
    onEditClick: (HabitEntity) -> Unit, // Callback para editar
    onDeleteClick: (HabitEntity) -> Unit // Callback para eliminar
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navegar a ProgressScreen pasando el nombre y la descripción del hábito
                navController.navigate("progress_screen/${habit.id}/${habit.name}/${habit.description}")
            }
            .background(Color.Gray.copy(alpha = 0.1f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = habit.name, fontWeight = FontWeight.Bold)

        Row {
            // Botón para editar
            IconButton(onClick = { onEditClick(habit) }) { // Llama al callback
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
            }
            // Botón para eliminar
            IconButton(onClick = { onDeleteClick(habit) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}


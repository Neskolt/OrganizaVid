package com.example.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.HabitDatabase
import com.example.db.dao.HabitDAO
import com.example.db.entities.HabitEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val habitDAO: HabitDAO = HabitDatabase.getDatabase(application).habitDAO()

    private val _habits = MutableLiveData<List<HabitEntity>>()
    val habits: LiveData<List<HabitEntity>> = _habits

    // Inicializar hábitos por defecto solo si no existen
    fun initializeDefaultHabitsIfNeeded(ageGroup: String) {
        viewModelScope.launch {
            // Verifica y carga hábitos predeterminados solo si no existen
            if (habitDAO.getHabitsByAgeGroup(ageGroup).isEmpty()) {
                val defaultHabits = when (ageGroup) {
                    "young" -> listOf(
                        HabitEntity(name = "Hacer ejercicio", description = "Ejercicio diario", ageGroup = "young"),
                        HabitEntity(name = "Comer saludable", description = "Comer alimentos saludables", ageGroup = "young"),
                        HabitEntity(name = "Meditar", description = "Meditar 10 minutos", ageGroup = "young")
                    )
                    "adult" -> listOf(
                        HabitEntity(name = "Leer", description = "Leer 20 minutos", ageGroup = "adult"),
                        HabitEntity(name = "Hacer ejercicio", description = "Ejercicio 30 minutos", ageGroup = "adult"),
                        HabitEntity(name = "Cuidar finanzas", description = "Planificar presupuesto mensual", ageGroup = "adult")
                    )
                    "old" -> listOf(
                        HabitEntity(name = "Cuidar la salud", description = "Hacer chequeos médicos", ageGroup = "old"),
                        HabitEntity(name = "Caminar", description = "Caminar 30 minutos", ageGroup = "old"),
                        HabitEntity(name = "Mantener la memoria activa", description = "Resolver acertijos o leer", ageGroup = "old")
                    )
                    else -> emptyList()
                }
                habitDAO.insertAll(defaultHabits)
            }

            // Sincroniza los datos con la pantalla
            getHabitsByAgeGroup(ageGroup)
        }
    }



    // Obtener hábitos según el grupo de edad
    fun getHabitsByAgeGroup(ageGroup: String) {
        viewModelScope.launch {
            _habits.postValue(habitDAO.getHabitsByAgeGroup(ageGroup))
        }
    }

    // Agregar un hábito
    fun addHabit(habit: HabitEntity) {
        viewModelScope.launch {
            val existingHabits = habitDAO.getHabitsByAgeGroup(habit.ageGroup)
            if (existingHabits.any { it.name == habit.name }) {
                // Opcional: Notificar al usuario que el hábito ya existe
                return@launch
            }

            habitDAO.insert(habit)
            val currentHabits = _habits.value?.toMutableList() ?: mutableListOf()
            currentHabits.add(habit)
            _habits.postValue(currentHabits)
        }
    }

    fun updateHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitDAO.update(habit)

            // Refrescar la lista actual en memoria
            val currentHabits = habitDAO.getHabitsByAgeGroup(habit.ageGroup)
            _habits.postValue(currentHabits)
        }
    }

    // Eliminar un hábito
    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitDAO.delete(habit)
            getHabitsByAgeGroup(habit.ageGroup) // Actualiza los hábitos después de eliminar uno
        }
    }
}
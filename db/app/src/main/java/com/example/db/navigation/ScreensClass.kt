package com.example.db.navigation

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object HabitScreen : Screen("habit_screen")
    object ProgressScreen : Screen("progress_screen")
}
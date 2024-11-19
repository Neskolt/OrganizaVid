import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.db.dao.UserDao
import com.example.db.entities.HabitEntity
import com.example.db.screens.HabitScreen
import com.example.db.screens.LoginScreen
import com.example.db.screens.ProgressScreen

@Composable
fun AppNavGraph(navController: NavHostController, userDao: UserDao) {
    NavHost(navController = navController, startDestination = "login_screen") {
        composable("login_screen") {
            LoginScreen(navController = navController, userDao = userDao)
        }

        // Pantalla de hábitos con parámetro ageGroup
        composable("habit_screen/{ageGroup}") { backStackEntry ->
            val ageGroup = backStackEntry.arguments?.getString("ageGroup") ?: ""
            HabitScreen(ageGroup = ageGroup, navController = navController)
        }

        // Pantalla de progreso para un hábito específico
        composable("progress_screen/{habitId}/{habitName}/{habitDescription}") { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId")?.toIntOrNull() ?: 0
            val habitName = backStackEntry.arguments?.getString("habitName") ?: ""
            val habitDescription = backStackEntry.arguments?.getString("habitDescription") ?: ""

            val habitEntity = HabitEntity(
                id = habitId,
                name = habitName,
                description = habitDescription,
                ageGroup = "default"
            )

            // Corregir para pasar habitId
            ProgressScreen(habito = habitEntity, habitId = habitId, context = navController.context)
        }
    }
}


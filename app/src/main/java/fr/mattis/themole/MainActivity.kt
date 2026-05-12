package fr.mattis.themole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.mattis.themole.presentation.game.GameScreen
import fr.mattis.themole.presentation.game.GameViewModel
import fr.mattis.themole.presentation.menu.MenuScreen
import fr.mattis.themole.presentation.navigation.Screen
import fr.mattis.themole.presentation.roles.RolesScreen
import fr.mattis.themole.presentation.roles.RolesViewModel
import fr.mattis.themole.presentation.roleSelection.RoleSelectionScreen
import fr.mattis.themole.presentation.roleSelection.RoleSelectionViewModel
import fr.mattis.themole.presentation.setup.SetupScreen
import fr.mattis.themole.presentation.setup.SetupViewModel
import fr.mattis.themole.ui.theme.TheMoleTheme

/**
 * Point d'entrée principal de l'application.
 * Gère le conteneur global et la configuration du NavHost.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TheMoleTheme {
                // Scaffold fournit la structure de base (TopBar, BottomBar, etc.)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Gestionnaire de navigation centralisé.
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MenuScreen.route,
        modifier = modifier
    ) {
        composable(route = Screen.MenuScreen.route) {
            MenuScreen(navController)
        }

        composable(route = Screen.SetupScreen.route) {
            val viewModel: SetupViewModel = viewModel()
            SetupScreen(navController, viewModel)
        }

        composable(route = Screen.RoleSelection.route) {
            val viewModel: RoleSelectionViewModel = viewModel()
            RoleSelectionScreen(navController, viewModel)
        }

        composable(route = Screen.RolesScreen.route) {
            val viewModel: RolesViewModel = viewModel()
            RolesScreen(navController, viewModel)
        }

        composable(route = Screen.GameScreen.route) {
            val viewModel: GameViewModel = viewModel()
            GameScreen(navController, viewModel)
        }
    }
}
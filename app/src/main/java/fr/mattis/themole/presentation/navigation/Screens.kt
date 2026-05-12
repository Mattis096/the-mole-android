package fr.mattis.themole.presentation.navigation

/**
 * Définition des destinations de navigation de l'application.
 * L'utilisation d'une sealed class garantit une gestion exhaustive des routes.
 *
 * @param route Le chemin unique utilisé par le NavHost pour identifier l'écran.
 */
sealed class Screen(val route: String) {

    data object MenuScreen : Screen("menu")

    data object SetupScreen : Screen("setup")

    data object RoleSelection : Screen("role_selection")

    data object RolesScreen : Screen("roles_display")

    data object GameScreen : Screen("game_main")

    override fun toString(): String = route
}
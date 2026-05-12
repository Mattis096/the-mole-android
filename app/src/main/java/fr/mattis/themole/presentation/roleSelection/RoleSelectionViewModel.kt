package fr.mattis.themole.presentation.roleSelection

import androidx.lifecycle.ViewModel
import fr.mattis.themole.data.model.Role
import fr.mattis.themole.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel gérant la sélection des rôles spéciaux avant le lancement de la partie.
 */
class RoleSelectionViewModel : ViewModel() {

    /**
     * Liste des rôles qui peuvent être activés ou désactivés par l'utilisateur.
     * On exclut les rôles de base (Agent et Taupe) qui sont toujours présents.
     */
    val availableRoles: List<Role> = Role.entries.filter { !it.isRequired }

    // Utilisation d'un Set pour éviter les doublons et faciliter l'ajout/suppression
    private val _selectedRoles = MutableStateFlow<Set<Role>>(
        setOf(Role.INFILTRATED, Role.HITMAN)
    )
    val selectedRoles: StateFlow<Set<Role>> = _selectedRoles.asStateFlow()

    /**
     * Ajoute ou retire un rôle de la sélection actuelle.
     *
     * @param role Le rôle à basculer.
     */
    fun toggleRole(role: Role) {
        _selectedRoles.update { currentSet ->
            if (currentSet.contains(role)) {
                currentSet - role
            } else {
                currentSet + role
            }
        }
    }

    /**
     * Finalise la configuration de la partie.
     * Enregistre les rôles choisis et déclenche la génération de la partie dans le Repository.
     */
    fun confirmSelection() {
        val rolesList = _selectedRoles.value.toList()
        GameRepository.apply {
            setOptionalRoles(rolesList)
            createGame()
        }
    }
}
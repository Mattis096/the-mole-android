package fr.mattis.themole.presentation.roles

import androidx.lifecycle.ViewModel
import fr.mattis.themole.data.model.Player
import fr.mattis.themole.data.model.Role
import fr.mattis.themole.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel gérant la phase de distribution et de révélation des rôles.
 * Assure la confidentialité des informations en ne révélant que les données autorisées
 * pour chaque rôle spécifique.
 */
class RolesViewModel : ViewModel() {

    // On récupère la liste immuable depuis le repository
    val players: List<Player> = GameRepository.players

    private val _currentPlayerIndex = MutableStateFlow(0)
    val currentPlayerIndex: StateFlow<Int> = _currentPlayerIndex.asStateFlow()

    private val _isRoleVisible = MutableStateFlow(false)
    val isRoleVisible: StateFlow<Boolean> = _isRoleVisible.asStateFlow()

    private val _allRolesSeen = MutableStateFlow(false)
    val allRolesSeen: StateFlow<Boolean> = _allRolesSeen.asStateFlow()

    /**
     * Joueur actuellement en train de consulter son écran.
     */
    val currentPlayer: Player?
        get() = players.getOrNull(_currentPlayerIndex.value)

    /**
     * Génère les informations secrètes spécifiques à chaque rôle.
     * Logique basée sur les mécaniques d'équilibrage et de déduction.
     */
    fun getExtraInfo(): String {
        val player = currentPlayer ?: return ""
        val role = player.role ?: return ""
        val allPlayers = players

        return when (role) {
            Role.INFILTRATED -> {
                val targets = allPlayers.filter { it.isMole && it.role != Role.THE_BRAIN }
                if (targets.isEmpty()) "Aucune menace détectée."
                else "Suspects identifiés : ${targets.joinToString { it.name }}"
            }

            Role.BODYGUARD -> {
                val targets = allPlayers.filter { it.role == Role.INFILTRATED || it.role == Role.MORGANA }
                "Cibles prioritaires (Infiltré / Imposteur) : ${targets.joinToString { it.name }}"
            }

            Role.MOLE, Role.HITMAN, Role.THE_BRAIN, Role.MORGANA -> {
                // Les taupes se voient entre elles, sauf la taupe aveugle
                val teammates = allPlayers.filter {
                    it.isMole && it.role != Role.BLIND_MOLE && it.id != player.id
                }
                if (teammates.isEmpty()) "Vous n'avez pas de complices visibles."
                else "Vos alliés infiltrés : ${teammates.joinToString { it.name }}"
            }

            else -> "Restez discret. Ne faites confiance à personne."
        }
    }

    /**
     * Alterne la visibilité du rôle pour permettre au joueur de le consulter secrètement.
     */
    fun toggleRoleVisibility() {
        _isRoleVisible.update { !it }
    }

    /**
     * Passe au joueur suivant ou finalise la phase de révélation.
     */
    fun nextPlayer() {
        if (_currentPlayerIndex.value < players.size - 1) {
            _currentPlayerIndex.update { it + 1 }
            _isRoleVisible.value = false
        } else {
            _allRolesSeen.value = true
        }
    }
}
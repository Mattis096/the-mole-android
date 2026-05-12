package fr.mattis.themole.presentation.setup

import androidx.lifecycle.ViewModel
import fr.mattis.themole.data.repository.GameRepository
import fr.mattis.themole.utils.GameConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel responsable de la gestion de la liste des joueurs avant le début de la partie.
 * Suit les principes de l'Unidirectional Data Flow (UDF).
 */
class SetupViewModel : ViewModel() {

    // Liste des noms des joueurs
    private val _players = MutableStateFlow<List<String>>(emptyList())
    val players: StateFlow<List<String>> = _players.asStateFlow()

    // Gestion des messages d'erreur pour l'interface
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Ajoute un nouveau joueur à la liste après validation.
     *
     * @param name Le nom saisi par l'utilisateur.
     */
    fun addPlayer(name: String) {
        val trimmedName = name.trim()

        when {
            trimmedName.isBlank() -> {
                _errorMessage.value = "Le nom ne peut pas être vide"
            }
            _players.value.size >= GameConstants.MAX_PLAYERS -> {
                _errorMessage.value = "Le jeu est limité à ${GameConstants.MAX_PLAYERS} joueurs"
            }
            _players.value.any { it.equals(trimmedName, ignoreCase = true) } -> {
                _errorMessage.value = "Ce nom est déjà utilisé par un autre agent"
            }
            else -> {
                _players.update { currentList -> currentList + trimmedName }
                _errorMessage.value = null
            }
        }
    }

    /**
     * Supprime un joueur de la liste.
     */
    fun removePlayer(name: String) {
        _players.update { currentList -> currentList - name }
        _errorMessage.value = null
    }

    /**
     * Vérifie si les conditions sont remplies pour passer à l'étape suivante.
     */
    fun canStartGame(): Boolean = _players.value.size >= GameConstants.MIN_PLAYERS

    /**
     * Enregistre la configuration actuelle dans le Repository.
     * Cette étape précède la sélection des rôles spéciaux.
     */
    fun confirmPlayers() {
        if (canStartGame()) {
            GameRepository.setPlayerNames(_players.value)
        } else {
            _errorMessage.value = "Il faut au moins ${GameConstants.MIN_PLAYERS} joueurs pour enquêter."
        }
    }

    /**
     * Efface le message d'erreur actuel.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
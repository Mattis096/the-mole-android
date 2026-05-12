package fr.mattis.themole.presentation.game

import androidx.lifecycle.ViewModel
import fr.mattis.themole.data.model.Mission
import fr.mattis.themole.data.model.MissionStatus
import fr.mattis.themole.data.model.Player
import fr.mattis.themole.data.model.Role
import fr.mattis.themole.data.repository.GameRepository
import fr.mattis.themole.utils.GameConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * États possibles du cycle de vie d'une manche.
 */
enum class GameStep {
    SELECTION,      // Le leader choisit l'équipe
    TEAM_VOTE,      // Tout le monde vote pour/contre l'équipe
    VOTE_PASS,      // Transition entre les votes secrets
    VOTE_ACTION,    // Vote secret (Succès/Échec) par un membre de l'équipe
    RESULT,         // Résultat de la mission
    ASSASSINATION   // Phase finale si les Agents ont gagné 3 missions
}

/**
 * ViewModel orchestrant la logique de jeu, les règles de mission et les conditions de victoire.
 */
class GameViewModel : ViewModel() {

    private val _step = MutableStateFlow(GameStep.SELECTION)
    val step: StateFlow<GameStep> = _step.asStateFlow()

    private val _missions = MutableStateFlow<List<Mission>>(emptyList())
    val missions: StateFlow<List<Mission>> = _missions.asStateFlow()

    private val _currentMissionIndex = MutableStateFlow(0)
    val currentMissionIndex: StateFlow<Int> = _currentMissionIndex.asStateFlow()

    val allPlayers = GameRepository.players

    private val _leaderIndex = MutableStateFlow(0)
    val leaderIndex: StateFlow<Int> = _leaderIndex.asStateFlow()

    private val _selectedPlayers = MutableStateFlow<Set<Player>>(emptySet())
    val selectedPlayers: StateFlow<Set<Player>> = _selectedPlayers.asStateFlow()

    private val _rejectionCount = MutableStateFlow(0)
    val rejectionCount: StateFlow<Int> = _rejectionCount.asStateFlow()

    private val _votingPlayerIndex = MutableStateFlow(0)
    val votingPlayerIndex: StateFlow<Int> = _votingPlayerIndex.asStateFlow()

    private var currentFailVotes = 0

    private val _winner = MutableStateFlow<String?>(null)
    val winner: StateFlow<String?> = _winner.asStateFlow()

    init {
        _missions.value = generateMissions(allPlayers.size)
    }

    /**
     * Génère la configuration des missions selon le nombre de joueurs (Règles officielles).
     */
    private fun generateMissions(count: Int): List<Mission> = when (count) {
        5 -> listOf(Mission(1, 2), Mission(2, 3), Mission(3, 2), Mission(4, 3), Mission(5, 3))
        6 -> listOf(Mission(1, 2), Mission(2, 3), Mission(3, 4), Mission(4, 3), Mission(5, 4))
        7 -> listOf(Mission(1, 2), Mission(2, 3), Mission(3, 3), Mission(4, 4, failsRequired = 2), Mission(5, 4))
        else -> listOf(Mission(1, 3), Mission(2, 4), Mission(3, 4), Mission(4, 5, failsRequired = 2), Mission(5, 5))
    }

    fun togglePlayerSelection(player: Player) {
        _selectedPlayers.update { current ->
            if (current.contains(player)) {
                current - player
            } else if (current.size < _missions.value[_currentMissionIndex.value].playersRequired) {
                current + player
            } else {
                current
            }
        }
    }

    fun confirmSelection() {
        _step.value = GameStep.TEAM_VOTE
    }

    /**
     * Résout le vote public sur l'équipe proposée.
     */
    fun resolveTeamVote(accepted: Boolean) {
        if (accepted) {
            _rejectionCount.value = 0
            _votingPlayerIndex.value = 0
            currentFailVotes = 0
            _step.value = GameStep.VOTE_PASS
        } else {
            _rejectionCount.update { it + 1 }
            if (_rejectionCount.value >= GameConstants.MAX_REJECTIONS) {
                _winner.value = "LES TAUPES (Vetos critiques)"
            } else {
                moveToNextLeader()
                _selectedPlayers.value = emptySet()
                _step.value = GameStep.SELECTION
            }
        }
    }

    fun startSecretVote() {
        _step.value = GameStep.VOTE_ACTION
    }

    /**
     * Enregistre un vote secret (Succès ou Sabotage).
     */
    fun submitSecretVote(isFail: Boolean) {
        if (isFail) currentFailVotes++

        if (_votingPlayerIndex.value < _selectedPlayers.value.size - 1) {
            _votingPlayerIndex.update { it + 1 }
            _step.value = GameStep.VOTE_PASS
        } else {
            completeMission()
        }
    }

    private fun completeMission() {
        val currentIndex = _currentMissionIndex.value

        _missions.update { currentMissions ->
            currentMissions.mapIndexed { index, mission ->
                if (index == currentIndex) mission.getCompletedMission(currentFailVotes)
                else mission
            }
        }

        checkWinConditions()
    }

    private fun checkWinConditions() {
        val currentMissions = _missions.value
        val successCount = currentMissions.count { it.status == MissionStatus.SUCCESS }
        val failCount = currentMissions.count { it.status == MissionStatus.FAILED }

        when {
            successCount >= 3 -> _step.value = GameStep.ASSASSINATION
            failCount >= 3 -> _winner.value = "VICTOIRE DES TAUPES"
            else -> _step.value = GameStep.RESULT
        }
    }

    fun confirmAssassination(target: Player) {
        if (target.role == Role.INFILTRATED) {
            _winner.value = "TAUPES (Infiltré démasqué !)"
        } else {
            _winner.value = "AGENTS (L'Infiltré est sauf !)"
        }
    }

    fun nextMission() {
        _currentMissionIndex.update { it + 1 }
        moveToNextLeader()
        _selectedPlayers.value = emptySet()
        _step.value = GameStep.SELECTION
    }

    private fun moveToNextLeader() {
        _leaderIndex.update { (it + 1) % allPlayers.size }
    }

    /**
     * Réinitialise toutes les données locales pour un retour au menu.
     */
    fun resetGameData() {
        _step.value = GameStep.SELECTION
        _currentMissionIndex.value = 0
        _rejectionCount.value = 0
        _selectedPlayers.value = emptySet()
        _winner.value = null
        _votingPlayerIndex.value = 0
        currentFailVotes = 0
        _missions.value = generateMissions(allPlayers.size)
    }

    /**
     * Lance une nouvelle manche avec les mêmes joueurs mais des rôles redistribués.
     */
    fun rematch() {
        GameRepository.rematch() // Mélange les rôles dans le Repo
        resetGameData()
    }
}
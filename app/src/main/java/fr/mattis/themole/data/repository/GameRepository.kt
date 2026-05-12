package fr.mattis.themole.data.repository

import fr.mattis.themole.data.model.Player
import fr.mattis.themole.data.model.Role
import fr.mattis.themole.utils.GameConstants

/**
 * Gestionnaire central de l'état de la partie.
 * Ce Repository utilise le pattern Singleton pour maintenir la cohérence des données
 * entre les différents écrans du jeu.
 */
object GameRepository {

    // Liste des joueurs avec leurs rôles attribués
    private val _players = mutableListOf<Player>()
    val players: List<Player> get() = _players.toList()

    // Cache pour conserver les noms entre le Setup et la création de partie
    private var lastPlayerNames = listOf<String>()

    // Rôles spéciaux sélectionnés par l'utilisateur
    private var selectedOptionalRoles = listOf(Role.INFILTRATED, Role.HITMAN)

    /**
     * Enregistre les noms des participants avant la distribution des rôles.
     */
    fun setPlayerNames(names: List<String>) {
        lastPlayerNames = names
    }

    /**
     * Met à jour la liste des rôles spéciaux actifs pour la prochaine partie.
     */
    fun setOptionalRoles(roles: List<Role>) {
        selectedOptionalRoles = roles
    }

    /**
     * Initialise une nouvelle partie : calcule la répartition, mélange les rôles
     * et crée les instances de [Player].
     */
    fun createGame() {
        val playerCount = lastPlayerNames.size
        if (playerCount < GameConstants.MIN_PLAYERS) return

        val rolePool = mutableListOf<Role>()

        // 1. Ajouter les rôles spéciaux sélectionnés
        rolePool.addAll(selectedOptionalRoles)

        // 2. Déterminer le nombre total de traîtres (Taupes)
        val totalMoleCount = calculateMoleCount(playerCount)

        // 3. Compléter le camp des Taupes si nécessaire
        val currentMolesInPool = rolePool.count { it.isMole }
        val molesToFill = (totalMoleCount - currentMolesInPool).coerceAtLeast(0)
        repeat(molesToFill) { rolePool.add(Role.MOLE) }

        // 4. Remplir le reste avec des Agents de terrain
        val remainingSlots = playerCount - rolePool.size
        repeat(remainingSlots.coerceAtLeast(0)) { rolePool.add(Role.FIELD_AGENT) }

        // 5. Mélanger et assigner
        val shuffledRoles = rolePool.shuffled()
        _players.clear()
        _players.addAll(lastPlayerNames.mapIndexed { index, name ->
            Player(id = index, name = name, role = shuffledRoles[index])
        })
    }

    /**
     * Calcule le nombre de traîtres en fonction du nombre total de joueurs.
     * Basé sur les règles d'équilibrage standard (type Avalon/Resistance).
     */
    private fun calculateMoleCount(playerCount: Int): Int = when (playerCount) {
        5, 6 -> 2
        7, 8, 9 -> 3
        10 -> 4
        else -> 2
    }

    /**
     * Relance une partie avec les mêmes paramètres (joueurs et rôles optionnels).
     */
    fun rematch() {
        if (lastPlayerNames.isNotEmpty()) {
            createGame()
        }
    }
}
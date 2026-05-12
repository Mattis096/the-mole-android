package fr.mattis.themole

import fr.mattis.themole.presentation.game.GameViewModel
import fr.mattis.themole.presentation.game.GameStep
import fr.mattis.themole.data.repository.GameRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GameViewModelTest {

    @Before
    fun setup() {
        // On utilise tes propres méthodes pour initialiser le jeu proprement
        val names = listOf("Alice", "Bob", "Charlie", "Dave", "Eve") // 5 joueurs
        GameRepository.setPlayerNames(names)

        // On vide les rôles optionnels pour avoir une répartition simple (3 Agents / 2 Taupes)
        GameRepository.setOptionalRoles(emptyList())

        // On génère les joueurs et les rôles
        GameRepository.createGame()
    }

    @Test
    fun `test du cycle de victoire des agents`() {
        val viewModel = GameViewModel()
        viewModel.resetGameData()

        assertEquals(GameStep.SELECTION, viewModel.step.value)

        // Simuler 3 missions réussies (3 succès = Victoire Agents)
        repeat(3) {
            viewModel.resolveTeamVote(true)   // Équipe acceptée
            viewModel.submitSecretVote(false) // Vote "SUCCESS"
            if (it < 2) viewModel.nextMission()
        }

        // Après 3 succès, l'étape suivante est l'assassinat
        assertEquals(GameStep.ASSASSINATION, viewModel.step.value)
    }

    @Test
    fun `test du compteur de rejets d'equipe`() {
        val viewModel = GameViewModel()
        viewModel.resetGameData()

        // On simule 5 rejets d'équipe consécutifs
        repeat(5) {
            viewModel.resolveTeamVote(false)
        }

        // Vérification de la condition de défaite par véto
        assertEquals("LES TAUPES (Vetos critiques)", viewModel.winner.value)
    }
}
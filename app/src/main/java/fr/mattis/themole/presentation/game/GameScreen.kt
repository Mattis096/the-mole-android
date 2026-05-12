package fr.mattis.themole.presentation.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.mattis.themole.data.model.MissionStatus
import fr.mattis.themole.data.model.Player
import fr.mattis.themole.presentation.composant.MoleButton
import fr.mattis.themole.presentation.navigation.Screen

@Composable
fun GameScreen(
    navController: NavController,
    viewModel: GameViewModel
) {
    val step by viewModel.step.collectAsState()
    val missions by viewModel.missions.collectAsState()
    val currentIndex by viewModel.currentMissionIndex.collectAsState()
    val selectedPlayers by viewModel.selectedPlayers.collectAsState()
    val leaderIndex by viewModel.leaderIndex.collectAsState()
    val rejectionCount by viewModel.rejectionCount.collectAsState()
    val votingIndex by viewModel.votingPlayerIndex.collectAsState()
    val winner by viewModel.winner.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- BARRE D'INFOS SYSTÈME ---
        SystemHeader(
            rejectionCount = rejectionCount,
            leaderName = viewModel.allPlayers[leaderIndex].name
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- PLATEAU DES MISSIONS ---
        MissionStatusRow(missions, currentIndex)

        Spacer(modifier = Modifier.height(32.dp))

        // --- LOGIQUE D'AFFICHAGE DES ÉTAPES ---
        Box(modifier = Modifier.weight(1f)) {
            val currentWinner = winner
            if (currentWinner != null) {
                GameOverView(
                    winner = currentWinner,
                    onQuit = {
                        viewModel.resetGameData()
                        navController.navigate(Screen.MenuScreen.route) {
                            popUpTo(Screen.MenuScreen.route) { inclusive = true }
                        }
                    },
                    onRematch = {
                        viewModel.rematch()
                        navController.navigate(Screen.RolesScreen.route)
                    }
                )
            } else {
                when (step) {
                    GameStep.SELECTION -> SelectionView(viewModel, missions[currentIndex].playersRequired, selectedPlayers)
                    GameStep.TEAM_VOTE -> TeamVoteView(viewModel.allPlayers[leaderIndex], selectedPlayers) { viewModel.resolveTeamVote(it) }
                    GameStep.VOTE_PASS -> VotePassView(selectedPlayers.toList()[votingIndex]) { viewModel.startSecretVote() }
                    GameStep.VOTE_ACTION -> VoteActionView(selectedPlayers.toList()[votingIndex]) { viewModel.submitSecretVote(it) }
                    GameStep.RESULT -> ResultView(missions[currentIndex]) { viewModel.nextMission() }
                    GameStep.ASSASSINATION -> AssassinationView(viewModel.allPlayers) { target -> viewModel.confirmAssassination(target) }
                }
            }
        }
    }
}

// --- SOUS-COMPOSANTS PRIVÉS ---

@Composable
private fun SystemHeader(rejectionCount: Int, leaderName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "VETO: $rejectionCount/5",
            style = MaterialTheme.typography.labelSmall,
            color = if (rejectionCount >= 4) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "MENEUR: ${leaderName.uppercase()}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MissionStatusRow(missions: List<fr.mattis.themole.data.model.Mission>, currentIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        missions.forEachIndexed { index, m ->
            MissionCircle(m.playersRequired, m.status, index == currentIndex)
        }
    }
}

@Composable
private fun SelectionView(vm: GameViewModel, required: Int, selected: Set<Player>) {
    val leaderIndex by vm.leaderIndex.collectAsState()
    val currentLeaderId = vm.allPlayers[leaderIndex].id

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("> SÉLECTION_UNITÉS", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text("REQUIS: $required AGENTS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)

        LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 16.dp)) {
            items(vm.allPlayers) { player ->
                val isLeader = player.id == currentLeaderId
                val isSelected = selected.contains(player)

                Surface(
                    onClick = { vm.togglePlayerSelection(player) },
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLeader) "[MENEUR] ${player.name}" else player.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }
                }
            }
        }

        MoleButton(
            text = "Proposer l'équipe",
            enabled = selected.size == required,
            onClick = { vm.confirmSelection() }
        )
    }
}

@Composable
private fun TeamVoteView(leader: Player, team: Set<Player>, onVoteResult: (Boolean) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("> VOTE_ASSEMBLÉE", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("DÉPLOIEMENT PROPOSÉ PAR ${leader.name.uppercase()} :", style = MaterialTheme.typography.labelSmall)

        Column(modifier = Modifier.padding(16.dp).background(MaterialTheme.colorScheme.surface).fillMaxWidth().padding(16.dp)) {
            team.forEach { Text("• ${it.name}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary) }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
            OutlinedButton(
                onClick = { onVoteResult(false) },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = MaterialTheme.shapes.extraSmall,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text("REFUSER", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.width(16.dp))
            MoleButton(
                text = "Accepter",
                modifier = Modifier.weight(1f),
                onClick = { onVoteResult(true) }
            )
        }
    }
}

@Composable
private fun ResultView(mission: fr.mattis.themole.data.model.Mission, onNext: () -> Unit) {
    val success = mission.status == MissionStatus.SUCCESS
    val accentColor = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = if (success) "> MISSION_RÉUSSIE" else "> MISSION_ÉCHOUÉE",
            color = accentColor,
            style = MaterialTheme.typography.headlineLarge
        )

        Surface(
            modifier = Modifier.padding(24.dp),
            color = accentColor.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, accentColor)
        ) {
            Text(
                text = "${mission.failVotesCount} SIGNAL(S) DE SABOTAGE DÉTECTÉ(S)",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        MoleButton(text = "Continuer la transmission", onClick = onNext)
    }
}

@Composable
fun MissionCircle(number: Int, status: MissionStatus, isCurrent: Boolean) {
    val borderColor = when (status) {
        MissionStatus.SUCCESS -> MaterialTheme.colorScheme.primary
        MissionStatus.FAILED -> MaterialTheme.colorScheme.error
        MissionStatus.PENDING -> if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .size(54.dp)
            .background(Color.Transparent, CircleShape)
            .border(if (isCurrent) 2.dp else 1.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = borderColor,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun VotePassView(player: Player, onConfirm: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "SABOTAGE_SECRET",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = "TRANSFÉREZ LE TERMINAL À\n[ ${player.name.uppercase()} ]",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 40.dp),
            color = Color.White
        )
        MoleButton(
            text = "Identification confirmée",
            onClick = onConfirm,
            modifier = Modifier.height(64.dp)
        )
    }
}

@Composable
private fun VoteActionView(player: Player, onVote: (Boolean) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "AGENT: ${player.name.uppercase()}",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
        Text(
            text = "DÉCISION OPÉRATIONNELLE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Bouton Valider (Toujours disponible)
        MoleButton(
            text = "Valider la mission",
            onClick = { onVote(false) },
            containerColor = Color(0xFF2E7D32), // Vert sombre tactique
            modifier = Modifier.height(80.dp)
        )

        // Bouton Saboter (Uniquement pour les taupes)
        if (player.role?.isMole == true) {
            Spacer(modifier = Modifier.height(24.dp))
            MoleButton(
                text = "Saboter la mission",
                onClick = { onVote(true) },
                containerColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.height(80.dp)
            )
        }
    }
}

@Composable
private fun GameOverView(winner: String, onQuit: () -> Unit, onRematch: () -> Unit) {
    val isMoleWin = winner.contains("TAUPES", ignoreCase = true)
    val winColor = if (isMoleWin) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "--- FIN DE TRANSMISSION ---",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = "VICTOIRE: $winner",
            style = MaterialTheme.typography.headlineLarge,
            color = winColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        MoleButton(
            text = "Nouvelle mission",
            onClick = onRematch
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onQuit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.extraSmall,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
        ) {
            Text("RÉINITIALISER LE TERMINAL", color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun AssassinationView(allPlayers: List<Player>, onConfirm: (Player) -> Unit) {
    var selectedByHitman by remember { mutableStateOf<Player?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "PROTOCOLE D'URGENCE",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "ÉLIMINEZ L'INFILTRÉ POUR\nINTERROMPRE LA VICTOIRE",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp),
            color = Color.White
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(allPlayers.filter { it.role?.isMole == false }) { player ->
                val isSelected = selectedByHitman == player
                Surface(
                    onClick = { selectedByHitman = player },
                    modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
                    color = if (isSelected) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.error else Color.Transparent),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = "[ ${player.name.uppercase()} ]",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.error else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MoleButton(
            text = "Confirmer l'exécution",
            enabled = selectedByHitman != null,
            onClick = { selectedByHitman?.let { onConfirm(it) } },
            containerColor = MaterialTheme.colorScheme.error
        )
    }
}
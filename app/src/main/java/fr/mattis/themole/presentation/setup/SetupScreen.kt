package fr.mattis.themole.presentation.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.mattis.themole.presentation.composant.MoleButton
import fr.mattis.themole.presentation.composant.PlayerListItem
import fr.mattis.themole.presentation.navigation.Screen
import fr.mattis.themole.utils.GameConstants

/**
 * Écran de configuration des joueurs.
 * Permet d'ajouter/supprimer les noms des agents avant le début de la mission.
 */
@Composable
fun SetupScreen(
    navController: NavController,
    viewModel: SetupViewModel
) {
    val players by viewModel.players.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    var playerName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // En-tête style Terminal
        Text(
            text = "> CONFIGURATION_AGENTS",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Zone d'input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = playerName,
                onValueChange = {
                    playerName = it
                    if (error != null) viewModel.clearError()
                },
                label = { Text("ID_AGENT", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                isError = error != null,
                supportingText = {
                    if (error != null) {
                        Text(text = "[ERREUR]: $error", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = {
                    viewModel.addPlayer(playerName)
                    if (error == null) playerName = ""
                },
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.small
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Liste des agents
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(players) { name ->
                PlayerListItem(
                    name = name,
                    onRemove = { viewModel.removePlayer(name) }
                )
            }
        }

        // Footer et validation
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!viewModel.canStartGame()) {
                Text(
                    text = "STATUT: ACCÈS_REFUSÉ (MIN. ${GameConstants.MIN_PLAYERS} AGENTS)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            MoleButton(
                text = "Initialiser briefing [${players.size}/${GameConstants.MAX_PLAYERS}]",
                enabled = viewModel.canStartGame(),
                onClick = {
                    viewModel.confirmPlayers()
                    navController.navigate(Screen.RoleSelection.route)
                }
            )
        }
    }
}
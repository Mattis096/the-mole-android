package fr.mattis.themole.presentation.roles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.mattis.themole.presentation.composant.MoleButton
import fr.mattis.themole.presentation.composant.RoleCard
import fr.mattis.themole.presentation.navigation.Screen
import fr.mattis.themole.utils.AudioHelper
import kotlinx.coroutines.launch

@Composable
fun RolesScreen(
    navController: NavController,
    viewModel: RolesViewModel
) {
    val isRoleVisible by viewModel.isRoleVisible.collectAsState()
    val allRolesSeen by viewModel.allRolesSeen.collectAsState()
    val currentPlayer = viewModel.currentPlayer
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Gestion de l'audio
    val audioHelper = remember { AudioHelper(context) }
    var isAudioPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose { audioHelper.destroy() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (allRolesSeen) {
            BriefingSection(
                isAudioPlaying = isAudioPlaying,
                onStartAudio = {
                    isAudioPlaying = true
                    scope.launch {
                        audioHelper.playBriefing(viewModel.players.mapNotNull { it.role })
                        isAudioPlaying = false
                    }
                },
                onStartGame = {
                    audioHelper.stop()
                    navController.navigate(Screen.GameScreen.route) {
                        popUpTo(Screen.SetupScreen.route) { inclusive = false }
                    }
                }
            )
        } else if (currentPlayer != null) {
            IdentificationSection(
                playerName = currentPlayer.name,
                isRoleVisible = isRoleVisible,
                onShowRole = { viewModel.toggleRoleVisibility() },
                onNext = { viewModel.nextPlayer() },
                roleCardContent = {
                    RoleCard(player = currentPlayer, extraInfo = viewModel.getExtraInfo())
                }
            )
        }
    }
}

@Composable
private fun BriefingSection(
    isAudioPlaying: Boolean,
    onStartAudio: () -> Unit,
    onStartGame: () -> Unit
) {
    Text(
        text = "> PHASE_DE_RECONNAISSANCE",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )
    Text(
        text = "Déposez le terminal au centre de la table pour le briefing audio.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 24.dp)
    )

    MoleButton(
        text = if (isAudioPlaying) "Transmission en cours..." else "Lancer le script audio",
        onClick = onStartAudio,
        enabled = !isAudioPlaying,
        containerColor = if (isAudioPlaying) MaterialTheme.colorScheme.surface else Color(0xFF2E7D32)
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedButton(
        onClick = onStartGame,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = MaterialTheme.shapes.extraSmall,
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        Icon(Icons.Default.SkipNext, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("DÉMARRER LA MISSION", color = Color.White)
    }
}

@Composable
private fun IdentificationSection(
    playerName: String,
    isRoleVisible: Boolean,
    onShowRole: () -> Unit,
    onNext: () -> Unit,
    roleCardContent: @Composable () -> Unit
) {
    Text(
        text = "DOSSIER: ${playerName.uppercase()}",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary
    )

    Spacer(modifier = Modifier.height(40.dp))

    if (!isRoleVisible) {
        Text(
            text = "REQUIS: IDENTIFICATION DE\n[ ${playerName.uppercase()} ]",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(48.dp))
        MoleButton(text = "Accéder aux données secrètes", onClick = onShowRole)
    } else {
        roleCardContent()
        Spacer(modifier = Modifier.height(48.dp))
        MoleButton(
            text = "Dossier consulté (Suivant)",
            onClick = onNext,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
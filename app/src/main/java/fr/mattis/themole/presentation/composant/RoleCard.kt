package fr.mattis.themole.presentation.composant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.mattis.themole.data.model.Player

/**
 * Affiche les informations secrètes d'un joueur.
 */
@Composable
fun RoleCard(player: Player, extraInfo: String, modifier: Modifier = Modifier) {
    val role = player.role
    val roleColor = if (role?.isMole == true) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, roleColor, MaterialTheme.shapes.extraSmall),
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = role?.displayName?.uppercase() ?: "INCONNU",
                style = MaterialTheme.typography.headlineLarge,
                color = roleColor
            )
            Text(
                text = if (role?.isMole == true) "ALLÉGEANCE: TAUPES" else "ALLÉGEANCE: AGENTS",
                style = MaterialTheme.typography.labelSmall,
                color = roleColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                color = roleColor.copy(alpha = 0.2f)
            )

            Text(
                text = role?.description ?: "",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            if (extraInfo.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .background(roleColor.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = extraInfo,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = roleColor
                    )
                }
            }
        }
    }
}
package fr.mattis.themole.presentation.composant

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.mattis.themole.data.model.Role

@Composable
fun RoleSelectionItem(
    role: Role,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // AlertRed pour les taupes, CyberCyan pour les agents
    val roleColor = if (role.isMole) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Surface(
        onClick = onToggle,
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isSelected) roleColor else Color.Transparent,
                shape = MaterialTheme.shapes.extraSmall
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = role.displayName.uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) roleColor else Color.White
                )
                Text(
                    text = role.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Switch(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = roleColor,
                    checkedTrackColor = roleColor.copy(alpha = 0.2f)
                )
            )
        }
    }
}
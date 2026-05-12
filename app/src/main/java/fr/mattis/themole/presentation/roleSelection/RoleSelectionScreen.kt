package fr.mattis.themole.presentation.roleSelection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.mattis.themole.presentation.composant.MoleButton
import fr.mattis.themole.presentation.composant.RoleSelectionItem
import fr.mattis.themole.presentation.navigation.Screen

/**
 * Écran permettant de sélectionner les rôles spéciaux actifs pour la partie.
 */
@Composable
fun RoleSelectionScreen(
    navController: NavController,
    viewModel: RoleSelectionViewModel
) {
    val selectedRoles by viewModel.selectedRoles.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // En-tête style Console
        Text(
            text = "> PROTOCOLES_SPÉCIAUX",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "SÉLECTIONNER LES UNITÉS ACTIVES DANS LE RÉSEAU",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Liste des rôles optionnels
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(viewModel.availableRoles) { role ->
                RoleSelectionItem(
                    role = role,
                    isSelected = selectedRoles.contains(role),
                    onToggle = { viewModel.toggleRole(role) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action finale
        MoleButton(
            text = "Déployer les profils",
            onClick = {
                viewModel.confirmSelection()
                navController.navigate(Screen.RolesScreen.route)
            }
        )
    }
}
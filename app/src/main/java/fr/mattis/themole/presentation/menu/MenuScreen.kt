package fr.mattis.themole.presentation.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.mattis.themole.R
import fr.mattis.themole.presentation.navigation.Screen
import fr.mattis.themole.presentation.composant.MoleButton

/**
 * Écran d'accueil principal du jeu.
 */
@Composable
fun MenuScreen(navController: NavController) {
    val cyberCyan = Color(0xFF00E5FF)
    val deepBlack = Color(0xFF0A0A0A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(deepBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Section visuelle du Logo
        LogoSection(accentColor = cyberCyan)

        // Section Titre et Sous-titre
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "THE MOLE",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 8.sp
                ),
                color = Color.White
            )

            Text(
                text = "SYSTÈME D'INFILTRATION ANALOGIQUE",
                style = MaterialTheme.typography.labelSmall,
                color = cyberCyan.copy(alpha = 0.7f)
            )
        }

        // Section Action utilisant le nouveau composant réutilisable
        MoleButton(
            text = "Initialiser la partie",
            onClick = { navController.navigate(Screen.SetupScreen.route) }
        )
    }
}

/**
 * Composant privé pour l'affichage du logo stylisé.
 */
@Composable
private fun LogoSection(accentColor: Color) {
    Surface(
        modifier = Modifier.size(150.dp),
        color = Color.Transparent,
        border = BorderStroke(2.dp, accentColor),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_mole_logo),
                contentDescription = "The Mole Logo",
                modifier = Modifier.size(100.dp),
                tint = accentColor
            )
        }
    }
}
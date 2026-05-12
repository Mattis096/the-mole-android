package fr.mattis.themole.data.model

import androidx.compose.runtime.Immutable

/**
 * Représente un participant à la partie.
 *
 * @property id Identifiant unique du joueur.
 * @property name Le pseudonyme affiché.
 * @property role Le rôle attribué (Loyal ou Taupe). Peut être nul avant le début de la partie.
 * @property isAlive État actuel du joueur (utile pour les phases d'élimination).
 */
@Immutable
data class Player(
    val id: Int,
    val name: String,
    val role: Role? = null,
    val isAlive: Boolean = true
) {
    /**
     * Vérifie si le joueur est la taupe.
     */
    val isMole: Boolean
        get() = role?.isMole == true

    /**
     * Retourne une copie du joueur avec un nouveau rôle.
     */
    fun withRole(newRole: Role): Player = this.copy(role = newRole)
}
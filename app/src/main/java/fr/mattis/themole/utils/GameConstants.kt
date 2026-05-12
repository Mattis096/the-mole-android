package fr.mattis.themole.utils

/**
 * Constantes globales régissant les règles du jeu et les contraintes de l'UI.
 */
object GameConstants {

    // --- RÈGLES DES MISSIONS ---
    const val MISSIONS_TO_WIN = 3
    const val MAX_REJECTIONS = 5 // La règle officielle de "The Resistance" est souvent 5, à ajuster selon ton game design
    const val DEFAULT_FAILS_REQUIRED = 1

    // Mission 4 (index 3) demande parfois 2 échecs à partir de 7 joueurs
    const val SPECIAL_MISSION_INDEX = 3
    const val SPECIAL_MISSION_FAILS_REQUIRED = 2

    // --- CONTRAINTES JOUEURS ---
    const val MIN_PLAYERS = 5
    const val MAX_PLAYERS = 10

    // Cohérence sur la longueur du nom
    const val MIN_NAME_LENGTH = 2
    const val MAX_NAME_LENGTH = 12

    // --- RÔLES ---
    const val MAX_SPECIAL_ROLES = 4
}
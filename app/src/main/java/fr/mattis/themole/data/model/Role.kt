package fr.mattis.themole.data.model

/**
 * Définit les rôles disponibles dans le jeu.
 *
 * @property displayName Le nom affiché dans l'interface.
 * @property description L'explication des pouvoirs/objectifs du rôle.
 * @property isMole Définit si le joueur appartient au camp des traîtres.
 * @property isRequired Définit si le rôle doit obligatoirement être présent (ex: Agent, Taupe).
 */
enum class Role(
    val displayName: String,
    val description: String,
    val isMole: Boolean,
    val isRequired: Boolean = false
) {
    // Rôles de base (Obligatoires)
    FIELD_AGENT("Agent", "Un agent loyal travaillant pour l'agence.", false, true),
    MOLE("La Taupe", "Un espion infiltré chargé de saboter les missions.", true, true),

    // Rôles Spéciaux (Optionnels - Inspirés des règles type Avalon)
    INFILTRATED("L'Infiltré", "Connaît l'identité des Taupes (sauf le Cerveau).", false),
    HITMAN("Le Tueur à Gages", "Doit identifier et éliminer l'Infiltré en fin de partie.", true),
    BODYGUARD("Le Garde du Corps", "Connaît l'Infiltré et l'Imposteur.", false),
    MORGANA("L'Imposteur", "Se fait passer pour l'Infiltré aux yeux du Garde du Corps.", true),
    BLIND_MOLE("La Taupe Aveugle", "Ne voit pas les autres Taupes et n'est pas vue par elles.", true),
    THE_BRAIN("Le Cerveau", "Le chef des Taupes. Invisible pour l'Infiltré.", true);

    /**
     * Retourne true si le rôle appartient au camp des agents loyaux.
     */
    val isLoyal: Boolean
        get() = !isMole

    companion object {
        /**
         * Retourne la liste des rôles optionnels pour l'écran de sélection.
         */
        fun getOptionalRoles(): List<Role> = entries.filter { !it.isRequired }
    }
}
package fr.mattis.themole.data.model

/**
 * Représente une mission au cours d'une partie.
 *
 * @property id Identifiant unique de la mission.
 * @property playersRequired Nombre de joueurs devant participer à cette mission.
 * @property failsRequired Nombre de votes "Echec" nécessaires pour faire rater la mission.
 * @property failVotesCount Nombre de votes "Echec" reçus (mis à jour à la fin de la mission).
 * @property participants Liste des joueurs sélectionnés pour la mission.
 * @property status État actuel de la mission (En attente, Réussie, Échouée).
 */
data class Mission(
    val id: Int,
    val playersRequired: Int,
    val failsRequired: Int = 1,
    val failVotesCount: Int = 0,
    val participants: List<Player> = emptyList(),
    val status: MissionStatus = MissionStatus.PENDING
) {
    /**
     * Indique si la mission a été accomplie avec succès.
     */
    val isSuccessful: Boolean
        get() = status == MissionStatus.SUCCESS

    /**
     * Calcule le nouvel état de la mission en fonction du nombre de votes négatifs.
     * Retourne une nouvelle instance de [Mission] pour respecter l'immuabilité.
     *
     * @param failCount Le nombre de votes "Echec" enregistrés.
     * @return Une copie de la mission avec le statut mis à jour.
     */
    fun getCompletedMission(failCount: Int): Mission {
        val newStatus = if (failCount >= failsRequired) {
            MissionStatus.FAILED
        } else {
            MissionStatus.SUCCESS
        }

        return this.copy(
            failVotesCount = failCount,
            status = newStatus
        )
    }
}

/**
 * Représente les différents états possibles d'une mission.
 */
enum class MissionStatus {
    PENDING,
    SUCCESS,
    FAILED
}
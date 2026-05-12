# The Mole : Digital Terminal

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-1.5-4285F4.svg?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-FF6F00.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)

**The Mole** est une application Android de jeu de rôle caché (inspirée de *The Resistance/Avalon*), conçue comme un terminal tactique cyber-punk. Elle transforme un smartphone en un outil d'immersion totale pour orchestrer des missions secrètes, des sabotages et des déductions sociales.

[Télécharger l'APK](https://github.com/Mattis096/the-mole-android/releases)

---

## Points Forts & UX

*   **Immersion "Cyber-Tactique"** : Interface sombre haute-fidélité utilisant des composants personnalisés (MoleButton) pour un aspect "HUD militaire".
*   **Briefing Audio Dynamique** : Intégration avancée du moteur TextToSpeech pour orchestrer la phase de nuit de manière autonome.
*   **Zéro Papier** : Gestion complète des votes secrets, des capacités spéciales (Cerveau, Infiltré, Garde du Corps) et des conditions de victoire.
*   **Flux de Jeu Robuste** : Système de machine à états (GameStep) empêchant toute erreur de manipulation ou triche accidentelle.

---

## Stack Technique

*   **UI** : Jetpack Compose (100% déclaratif).
*   **Logique** : StateFlow & SharedFlow pour une gestion d'état réactive et immuable.
*   **Architecture** : MVVM (Model-View-ViewModel) structuré.
*   **Hardware** : Moteur `TextToSpeech` avec synchronisation via Coroutines (`suspendCancellableCoroutine`).
*   **Navigation** : Jetpack Navigation avec gestion sécurisée de la backstack.

---

## Défis Techniques Relevés

### 1. Synchronisation Audio & Coroutines
L'un des plus gros défis a été de créer un briefing audio fluide. Au lieu d'utiliser des pauses fixes (`delay`), l'application utilise une implémentation personnalisée de `UtteranceProgressListener`.
> **Solution** : Le code suspend la coroutine jusqu'à ce que le système termine réellement de parler, garantissant une immersion parfaite peu importe la longueur des noms ou la vitesse du processeur.

### 2. UI Custom & Design System
Développement d'un composant de bouton personnalisé (`MoleButton`) gérant les états de clic, les bordures néon et le feedback visuel, afin de maintenir une cohérence esthétique "Terminal" sur tous les écrans du jeu.

---

## Architecture du Projet

```text
fr.mattis.themole
├── data
│   ├── model       # Modèles de données (Player, Role, Mission)
│   └── repository  # Gestion de l'état global des joueurs
├── presentation
│   ├── game        # Logique de jeu (ViewModel & Screens)
│   └── setup       # Configuration de la partie
├── ui.theme        # Thème Cyberpunk personnalisé
└── utils           # AudioHelper, GameConstants
```

## Licence

Ce projet est sous licence MIT. Libre à vous de l'utiliser et de l'améliorer !
Développé par Mattis — Étudiant & Passionné Android.

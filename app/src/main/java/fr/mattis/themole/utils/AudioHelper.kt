package fr.mattis.themole.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import fr.mattis.themole.data.model.Role
import java.util.*
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.delay

class AudioHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.apply {
                language = Locale.FRENCH
                setPitch(0.9f) // Un ton légèrement plus grave pour l'ambiance "IA de mission"
                setSpeechRate(1.0f)

                setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        // Cette callback est appelée quand le TTS finit de parler
                        synchronized(this@AudioHelper) {
                            continuation?.resume(Unit)
                            continuation = null
                        }
                    }
                    override fun onError(utteranceId: String?) {
                        synchronized(this@AudioHelper) {
                            continuation?.resume(Unit)
                            continuation = null
                        }
                    }
                })
            }
            isReady = true
        }
    }

    private var continuation: kotlin.coroutines.Continuation<Unit>? = null

    /**
     * Parle et suspend la coroutine jusqu'à la fin de la phrase.
     */
    private suspend fun speakAndWait(text: String) = suspendCancellableCoroutine<Unit> { cont ->
        if (!isReady || tts == null) {
            cont.resume(Unit)
            return@suspendCancellableCoroutine
        }

        synchronized(this) {
            continuation = cont
            val id = System.currentTimeMillis().toString()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
        }
    }

    private suspend fun countdown(from: Int) {
        for (i in from downTo 1) {
            speakAndWait(i.toString())
            delay(400) // Petit silence entre les chiffres
        }
    }

    suspend fun playBriefing(activeRoles: List<Role>) {
        // Ambiance de départ
        speakAndWait("Initialisation du protocole de reconnaissance.")
        delay(1000)
        speakAndWait("Tout le monde ferme les yeux et tend son poing fermé devant lui.")
        delay(2000)

        // 1. LES TAUPES (Sauf Blind Mole)
        speakAndWait("Les Taupes, à l'exception de la Taupe Aveugle, ouvrez les yeux et identifiez vos complices.")
        countdown(7)
        speakAndWait("Les Taupes, fermez les yeux.")
        delay(1500)

        // 2. L'INFILTRÉ (Merlin)
        if (activeRoles.contains(Role.INFILTRATED)) {
            speakAndWait("Les Taupes, à l'exception du Cerveau, levez le pouce.")
            speakAndWait("L'Infiltré, ouvre les yeux pour identifier les menaces.")
            countdown(7)
            speakAndWait("L'Infiltré, ferme les yeux. Les Taupes, baissez les pouces.")
            delay(1500)
        }

        // 3. LE GARDE DU CORPS (Percival)
        if (activeRoles.contains(Role.BODYGUARD)) {
            speakAndWait("L'Infiltré et l'Imposteur, levez le pouce.")
            speakAndWait("Garde du Corps, ouvre les yeux pour identifier votre cible à protéger.")
            countdown(7)
            speakAndWait("Garde du Corps, ferme les yeux. Baissez les pouces.")
            delay(1500)
        }

        // Conclusion
        speakAndWait("Fin du protocole. Tout le monde ouvre les yeux.")
        speakAndWait("La mission commence maintenant.")
    }

    fun stop() {
        tts?.stop()
    }

    fun destroy() {
        tts?.shutdown()
        tts = null
    }
}
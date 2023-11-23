package com.example.storeteller;

import android.net.Uri;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.example.storeteller.ui.play.PlayFragment;

public class TtsUtteranceListener extends UtteranceProgressListener {

    private final PlayFragment.OnSynthesisCompleteListener listener;
    private final Uri audioFileUri;

    // Constructeur qui prend un auditeur et une URI pour le fichier audio généré
    public TtsUtteranceListener(PlayFragment.OnSynthesisCompleteListener listener, Uri audioFileUri) {
        this.listener = listener;
        this.audioFileUri = audioFileUri;
    }

    // Appelé lorsque la synthèse vocale commence
    @Override
    public void onStart(String utteranceId) {
        Log.d("TtsUtteranceListener", "Synthesis started. Generating audio files...");
    }

    // Appelé lorsque la synthèse vocale est terminée
    @Override
    public void onDone(String utteranceId) {
        if (listener != null) {
            // Informe l'auditeur que la synthèse vocale est terminée avec l'URI du fichier audio généré
            listener.onSynthesisComplete(audioFileUri);
        }
        Log.d("TtsUtteranceListener", "Synthesis complete. Generation complete...");
    }

    // Appelé en cas d'erreur lors de la synthèse vocale
    @Override
    public void onError(String utteranceId) {
        Log.e("TtsUtteranceListener", "Error during synthesis.");
    }
}

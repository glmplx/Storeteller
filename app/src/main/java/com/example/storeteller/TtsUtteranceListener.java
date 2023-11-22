package com.example.storeteller;

import android.net.Uri;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.example.storeteller.ui.play.PlayFragment;

public class TtsUtteranceListener extends UtteranceProgressListener {

    private PlayFragment.OnSynthesisCompleteListener listener;
    private Uri audioFileUri;

    public TtsUtteranceListener(PlayFragment.OnSynthesisCompleteListener listener, Uri audioFileUri) {
        this.listener = listener;
        this.audioFileUri = audioFileUri;
    }

    @Override
    public void onStart(String utteranceId) {
        Log.d("start", "generating audio files...");
    }

    @Override
    public void onDone(String utteranceId) {
        // Appeler l'interface pour informer le fragment que la synthèse est terminée
        if (listener != null) {
            listener.onSynthesisComplete(audioFileUri);
        }
        Log.d("done", "generation complete...");
    }

    @Override
    public void onError(String utteranceId) {
        Log.d("error", "error.");
    }
}
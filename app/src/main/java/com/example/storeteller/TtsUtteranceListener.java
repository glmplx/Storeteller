package com.example.storeteller;

import android.net.Uri;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.example.storeteller.ui.play.PlayFragment;

public class TtsUtteranceListener extends UtteranceProgressListener {

    private final PlayFragment.OnSynthesisCompleteListener listener;
    private final Uri audioFileUri;

    // Constructor that takes a listener and a URI for the generated audio file
    public TtsUtteranceListener(PlayFragment.OnSynthesisCompleteListener listener, Uri audioFileUri) {
        this.listener = listener;
        this.audioFileUri = audioFileUri;
    }

    // Called when text-to-speech starts
    @Override
    public void onStart(String utteranceId) {
        Log.d("TtsUtteranceListener", "Synthesis started. Generating audio files...");
    }

    // Called when text-to-speech is complete
    @Override
    public void onDone(String utteranceId) {
        if (listener != null) {
            // Informs the listener that text-to-speech is complete with the URI of the generated audio file
            listener.onSynthesisComplete(audioFileUri);
        }
        Log.d("TtsUtteranceListener", "Synthesis complete. Generation complete...");
    }

    // Called in case of speech synthesis error
    @Override
    public void onError(String utteranceId) {
        Log.e("TtsUtteranceListener", "Error during synthesis.");
    }
}

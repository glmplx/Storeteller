package com.example.storeteller;

import android.net.Uri;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.example.storeteller.ui.play.PlayFragment;

public class TtsUtteranceListener extends UtteranceProgressListener {

    private final PlayFragment.OnSynthesisCompleteListener listener;
    private final Uri audioFileUri;

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
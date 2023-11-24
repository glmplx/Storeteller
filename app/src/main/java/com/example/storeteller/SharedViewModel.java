package com.example.storeteller;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.Locale;

public class SharedViewModel extends ViewModel {

    // Uri of the selected PDF file
    private Uri selectedFileUri;

    // Text extracted from the selected PDF file
    private String textExtract;

    // Local selected for text-to-speech
    private Locale selectedLocale;

    // Pitch for speech synthesis
    private float pitch = 1.0f;

    // Speed for speech synthesis
    private float speed = 1.0f;

    // Methods for defining properties

    public void setSelectedFileUri(Uri uri) {
        selectedFileUri = uri;
    }

    public void setText(String text) {
        textExtract = text;
    }

    public void setSelectedLocale(Locale locale) {
        selectedLocale = locale;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    // Methods for retrieving property values

    public Uri getSelectedFileUri() {
        return selectedFileUri;
    }

    public String getText() {
        return textExtract;
    }

    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    public float getPitch() {
        return pitch;
    }

    public float getSpeed() {
        return speed;
    }
}

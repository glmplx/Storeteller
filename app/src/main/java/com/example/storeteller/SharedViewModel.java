package com.example.storeteller;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.Locale;

public class SharedViewModel extends ViewModel {
    private Uri selectedFileUri;
    private String textExtract;
    private Locale selectedLocale;
    private float pitch = 1.0f;
    private float speed = 1.0f;

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

package com.example.storeteller;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.Locale;

public class SharedViewModel extends ViewModel {

    // Uri du fichier PDF sélectionné
    private Uri selectedFileUri;

    // Texte extrait du fichier PDF sélectionné
    private String textExtract;

    // Locale sélectionnée pour la synthèse vocale
    private Locale selectedLocale;

    // Hauteur tonale (pitch) pour la synthèse vocale
    private float pitch = 1.0f;

    // Vitesse de la synthèse vocale
    private float speed = 1.0f;

    // Méthodes pour définir les différentes propriétés

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

    // Méthodes pour récupérer les valeurs des propriétés

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

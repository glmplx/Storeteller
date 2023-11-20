package com.example.storeteller;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private Uri selectedFileUri;
    private String textExtract;

    public void setSelectedFileUri(Uri uri) {
        selectedFileUri = uri;
    }

    public void setText(String text) {
        textExtract = text;
    }

    public Uri getSelectedFileUri() {
        return selectedFileUri;
    }

    public String getText() {
        return textExtract;
    }
}

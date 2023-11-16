package com.example.storeteller;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Uri> selectedFileUri = new MutableLiveData<Uri>();

    public void setSelectedFileUri(Uri uri) {
        selectedFileUri.setValue(uri);
    }

    public LiveData<Uri> getSelectedFileUri() {
        return selectedFileUri;
    }
}

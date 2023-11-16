package com.example.storeteller.ui.play;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.SharedViewModel;
import com.github.barteksc.pdfviewer.PDFView;

public class PlayFragment extends Fragment{

    private PDFView pdfView;
    private Uri selectedFileUri;
    private SharedViewModel sharedViewModel;

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pdfView = view.findViewById(R.id.PDFView);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedFileUri().observe(getViewLifecycleOwner(), selectedFileUri  -> {
            if (selectedFileUri != null) {
                // Update the PDFView with the selected file URI
                pdfView.fromUri(selectedFileUri).load();
            }
        });
    }

    public void setSelectedFile(Uri selectedFileUri) {
    }
}

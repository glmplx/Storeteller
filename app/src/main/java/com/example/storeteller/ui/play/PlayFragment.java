package com.example.storeteller.ui.play;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.SharedViewModel;
import com.github.barteksc.pdfviewer.PDFView;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PlayFragment extends Fragment {

    private PDFView pdfView;
    private TextView textView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_play, container, false);
        pdfView = view.findViewById(R.id.PDFView);
        textView = view.findViewById(R.id.textView);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedFileUri().observe(getViewLifecycleOwner(), selectedFileUri -> {
            if (selectedFileUri != null) {
                // Perform PDF to text conversion here
                convertPdfToText(selectedFileUri);
                pdfView.fromUri(selectedFileUri).load();
            }
        });
        return view;
    }

    private void convertPdfToText(Uri selectedFileUri) {
        String path = selectedFileUri.getPath();
        assert path != null;
        File file = new File(path);
        try (PDDocument document = PDDocument.load(file)) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            StringBuilder textContent = new StringBuilder();

            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                // Set the page interval to extract.
                stripper.setStartPage(p);
                stripper.setEndPage(p);

                // Let the magic happen
                String text = stripper.getText(document);
                textContent.append(text.trim());
            }

            // Set the extracted text to the TextView
            textView.setText(textContent.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

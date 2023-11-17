package com.example.storeteller.ui.play;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.SharedViewModel;
import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.util.Locale;

public class PlayFragment extends Fragment {

    private static final int READ_PDF_REQUEST = 1;
    private PDFView pdfView;
    private TextView textView;
    private Button playButton;
    private TextToSpeech tts;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_play, container, false);

        pdfView = view.findViewById(R.id.PDFView);

        playButton = view.findViewById(R.id.playButton);

        textView = view.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.US);
            }
        });

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedFileUri().observe(getViewLifecycleOwner(), selectedFileUri -> {
            if (selectedFileUri != null) {
                pdfView.fromUri(selectedFileUri).load();
                final String id = DocumentsContract.getDocumentId(selectedFileUri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContext().getContentResolver().query(contentUri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String pdfPath = cursor.getString(column_index);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String stringParser = "";
                        try {
                            PdfReader pdfReader = new PdfReader(pdfPath);
                            stringParser = PdfTextExtractor.getTextFromPage(pdfReader, 1).trim();
                            pdfReader.close();
                            textView.setText(stringParser);
                            tts.speak(stringParser, TextToSpeech.QUEUE_FLUSH, null, null);
                        } catch (IOException e) {
                            stringParser = "Problem with converter";
                            textView.setText(stringParser);
                            tts.speak(stringParser, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }
                });
            }
        });
        return view;
    }
}

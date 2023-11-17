package com.example.storeteller.ui.play;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.FilePathHelper;
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
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FilePathHelper filePathHelper = new FilePathHelper();
                        String uriString = "";

                        if (filePathHelper.getPathnew(selectedFileUri, getContext()) != null) {
                            uriString = filePathHelper.getPathnew(selectedFileUri, getContext()).toLowerCase();
                        } else {
                            uriString = filePathHelper.getFilePathFromURI(selectedFileUri, getContext()).toLowerCase();
                        }

                        // Replace the cache directory with the desired path
                        //uriString = uriString.replace("/android/data/com.example.storeteller/cache/", "/Download/");

                        //String fileName = getFileNameFromUri(selectedFileUri);
                        //String[] uriParts = uriString.split("/");
                        //uriParts[uriParts.length - 1] = fileName;
                        //uriString = TextUtils.join("/", uriParts);

                        Log.d("path",uriString);

                        String stringParser = "";

                        try {
                            PdfReader pdfReader = new PdfReader(uriString);
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

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    fileName = cursor.getString(nameIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }
}

package com.example.storeteller.ui.play;

import android.net.Uri;
import android.os.Bundle;
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

import java.util.Locale;

public class PlayFragment extends Fragment {

    private PDFView pdfView;
    private TextView textView;
    private Button playButton;
    private TextToSpeech tts;
    private Uri selectedFileUri;
    private String pdfText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_play, container, false);

        pdfView = view.findViewById(R.id.PDFView);

        playButton = view.findViewById(R.id.playButton);

        textView = view.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        this.selectedFileUri = sharedViewModel.getSelectedFileUri();
        this.pdfText = sharedViewModel.getText();

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.US);
            }
        });

        if (selectedFileUri != null) {
            pdfView.fromUri(selectedFileUri).load();
            textView.setText(pdfText);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tts.speak(pdfText, TextToSpeech.QUEUE_FLUSH,null, null);
                }
            });
        }
        return view;
    }
}

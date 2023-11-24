package com.example.storeteller.ui.play;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.SharedViewModel;
import com.example.storeteller.TtsUtteranceListener;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class PlayFragment extends Fragment {

    // UI components
    private Button playButton, rewindButton, forwardButton;
    private ImageView loadLogo;
    private TextToSpeech tts;
    private Uri selectedFileUri;
    private Uri audioFileUri;
    private MediaPlayer mediaPlayer;
    private String pdfText;
    private SeekBar playbackSeekBar;
    private Locale selectedLocale;
    private Runnable runnable;
    private Handler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        // Initialize UI components
        PDFView pdfView = view.findViewById(R.id.PDFView);
        loadLogo = view.findViewById(R.id.loadLogo);
        loadLogo.setVisibility(View.GONE);

        playButton = view.findViewById(R.id.playButton);
        playButton.setText(R.string.play);

        rewindButton = view.findViewById(R.id.rewindButton);
        forwardButton = view.findViewById(R.id.forwardButton);

        TextView textView = view.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(R.string.emptyPlay);

        playbackSeekBar = view.findViewById(R.id.playbackSeekBar);

        handler = new Handler();

        mediaPlayer = new MediaPlayer();

        // Get the SharedViewModel for communication between fragments
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        selectedFileUri = sharedViewModel.getSelectedFileUri();
        pdfText = sharedViewModel.getText();

        if (selectedFileUri != null) {
            pdfView.fromUri(selectedFileUri).load();
            textView.setText(pdfText);
            selectedLocale = sharedViewModel.getSelectedLocale();
            if (selectedLocale == null) selectedLocale = Locale.getDefault();

            // Initialize TextToSpeech
            tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        // Set language, pitch, and speech rate
                        tts.setLanguage(selectedLocale);
                        tts.setPitch(sharedViewModel.getPitch());
                        tts.setSpeechRate(sharedViewModel.getSpeed());

                        // Set up TTS file path
                        File sddir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Storeteller");
                        if (!sddir.exists()) {
                            sddir.mkdir();
                        }
                        String filePath = sddir.getAbsolutePath() + "/" + getFileNameFromUri(selectedFileUri).replace(".pdf", "") + ".wav";
                        File file = new File(filePath);

                        // Disable UI during TTS synthesis
                        disableInterface();
                        loadLogo.setVisibility(View.VISIBLE);
                        loadLogo.animate().rotationBy(360).start();

                        // Synthesize TTS to file
                        tts.synthesizeToFile(pdfText, new Bundle(), file, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

                        // Set audio file URI
                        audioFileUri = Uri.fromFile(file);

                        // Set up TTS listener
                        tts.setOnUtteranceProgressListener(new TtsUtteranceListener(new OnSynthesisCompleteListener() {
                            @Override
                            public void onSynthesisComplete(Uri audioFileUri) {
                                handleSynthesisCompletion(audioFileUri);
                            }
                        }, audioFileUri));
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (selectedFileUri != null) {

            if (mediaPlayer != null) {
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle play/pause button click
                        playbackSeekBar.setMax(mediaPlayer.getDuration());
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            updateSeekBar();
                            playButton.setText(R.string.pause);
                        } else {
                            mediaPlayer.pause();
                            updateSeekBar();
                            playButton.setText(R.string.play);
                        }
                    }
                });

                rewindButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle rewind button click
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int newPosition = currentPosition - 5000; // 5 seconds in milliseconds

                        if (newPosition < 0) {
                            newPosition = 0;
                        }

                        mediaPlayer.seekTo(newPosition);
                        playbackSeekBar.setProgress(newPosition);
                    }
                });

                forwardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle forward button click
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int newPosition = currentPosition + 5000; // 5 seconds in milliseconds
                        int duration = mediaPlayer.getDuration();

                        if (newPosition > duration) {
                            newPosition = duration;
                        }

                        mediaPlayer.seekTo(newPosition);
                        playbackSeekBar.setProgress(newPosition);
                    }
                });

                playbackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // Handle seek bar progress change
                        if (fromUser) {
                            mediaPlayer.seekTo(progress);
                            seekBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        }
    }

    public void updateSeekBar() {
        // Update seek bar progress
        int currPos = mediaPlayer.getCurrentPosition();
        playbackSeekBar.setProgress(currPos);

        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar();
            }
        };
        handler.postDelayed(runnable, 10);
    }

    private String getFileNameFromUri(Uri uri) {
        // Get file name from URI
        String fileName = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
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

    public interface OnSynthesisCompleteListener {
        void onSynthesisComplete(Uri audioFileUri);
    }

    private void handleSynthesisCompletion(Uri audioFileUri) {
        // Handle TTS synthesis completion
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadLogo.setRotation(0);
                loadLogo.setVisibility(View.GONE);
                enableInterface();
            }
        });
        mediaPlayer = MediaPlayer.create(getActivity(), audioFileUri);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setText(R.string.play);
                playbackSeekBar.setProgress(0);
                handler.removeCallbacks(runnable);
            }
        });
    }

    public void enableInterface() {
        // Enable UI components
        playButton.setEnabled(true);
        forwardButton.setEnabled(true);
        rewindButton.setEnabled(true);
    }

    public void disableInterface() {
        // Disable UI components
        playButton.setEnabled(false);
        forwardButton.setEnabled(false);
        rewindButton.setEnabled(false);
    }

    @Override
    public void onDestroy() {
        // Clean up resources
        super.onDestroy();
    }

}

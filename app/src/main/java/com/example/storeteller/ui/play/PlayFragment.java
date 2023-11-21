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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.SharedViewModel;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.Locale;

public class PlayFragment extends Fragment {

    private String audioFileName = "";
    private String fileId = "";
    private PDFView pdfView;
    private TextView textView;
    private Button playButton,rewindButton, forwardButton;
    private TextToSpeech tts;
    private Uri selectedFileUri;
    private MediaPlayer mediaPlayer;
    private String pdfText;
    private SeekBar playbackSeekBar;
    private Runnable runnable;
    private Handler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_play, container, false);

        pdfView = view.findViewById(R.id.PDFView);

        playButton = view.findViewById(R.id.playButton);
        playButton.setText(R.string.play);

        rewindButton = view.findViewById(R.id.rewindButton);
        forwardButton = view.findViewById(R.id.forwardButton);

        textView = view.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        playbackSeekBar = view.findViewById(R.id.playbackSeekBar);

        handler = new Handler();

        mediaPlayer = new MediaPlayer();


        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        this.selectedFileUri = sharedViewModel.getSelectedFileUri();
        this.pdfText = sharedViewModel.getText();

        if (selectedFileUri != null) {
            pdfView.fromUri(selectedFileUri).load();
            textView.setText(pdfText);
            fileId = getFileNameFromUri(selectedFileUri).replace(".pdf", "");

            tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.US);
                        createFile(fileId);
                        saveToAudioFile(pdfText,fileId);
                        File file = new File(audioFileName);
                        Uri audioFileUri = Uri.fromFile(file);
                        if(audioFileUri != null) {
                            mediaPlayer = MediaPlayer.create(getActivity(), audioFileUri);

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    // Called when playback is completed
                                    playButton.setText(R.string.play);
                                    playbackSeekBar.setProgress(0);
                                    handler.removeCallbacks(runnable); // Stop updating the seekbar
                                }
                            });
                        }
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mediaPlayer != null) {

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playbackSeekBar.setMax(mediaPlayer.getDuration());
                    if(!mediaPlayer.isPlaying()){
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
                    if(fromUser){
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

    public void updateSeekBar(){
        int currPos = mediaPlayer.getCurrentPosition();
        playbackSeekBar.setProgress(currPos);

        runnable = new Runnable() {
            @Override
            public void run() {
                updateSeekBar();
            }
        };
        handler.postDelayed(runnable,10);
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

    private  void createFile(String fileId){
        File sddir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Storeteller");
        if(!sddir.exists()){
            boolean isDirectoryCreated = sddir.mkdirs();
            if(!isDirectoryCreated){
                Toast.makeText(getActivity(),"Can't create directory so save audio",Toast.LENGTH_SHORT).show();
            }
        }
        sddir.mkdirs();
        audioFileName = sddir.getAbsolutePath() + "/" + fileId + System.currentTimeMillis() + ".wav";
        Log.d("view",audioFileName);
    }

    private void saveToAudioFile(String pdfText, String fileId){
        tts.synthesizeToFile(pdfText, null, new File(audioFileName), fileId);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}
package com.example.storeteller.ui.play;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.SharedViewModel;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class PlayFragment extends Fragment {

    private String envPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Documents";
    private PDFView pdfView;
    private TextView textView;
    private Button playButton;
    private TextToSpeech tts;
    private Uri audioFileUri;
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
        playButton.setText("Play");

        textView = view.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        playbackSeekBar = view.findViewById(R.id.playbackSeekBar);

        mediaPlayer = new MediaPlayer();

        handler = new Handler();

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        this.selectedFileUri = sharedViewModel.getSelectedFileUri();
        this.pdfText = sharedViewModel.getText();

        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    HashMap<String, String> myHashRender = new HashMap<String, String>();
                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, pdfText);

                    String destFileName = envPath + "/" + "tts_file.wav";

                    int sr = tts.synthesizeToFile(pdfText, myHashRender, destFileName);
                    File fileTTS = new File(destFileName);

                    audioFileUri = Uri.fromFile(fileTTS);
                }
            }
        });



        if (selectedFileUri != null) {
            pdfView.fromUri(selectedFileUri).load();
            textView.setText(pdfText);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play();
                    // TODO bouton play et pause qui change
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
                    // TODO recupérer la position et l'adapté à l'audio
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });



        }

        return view;
    }

    public void play(){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(getActivity(),audioFileUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playbackSeekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                updateSeekBar();
            }
        });

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
        handler.postDelayed(runnable,1000);
    }

}
package com.example.storeteller.ui.play;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;

import com.example.storeteller.R;

public class PlayFragment extends Fragment {

    private Button playButton, pauseButton, rewindButton, forwardButton;
    private SeekBar playbackSeekBar;
    private Uri selectedFileUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        playButton = view.findViewById(R.id.playButton);
        pauseButton = view.findViewById(R.id.pauseButton);
        //rewindButton = view.findViewById(R.id.rewindButton);
        //forwardButton = view.findViewById(R.id.forwardButton);
        playbackSeekBar = view.findViewById(R.id.playbackSeekBar);

        // Set onClickListener for play button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement play logic (check if the file is already converted, if not, convert and play)
            }
        });

        // Set onClickListener for pause button
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement pause logic
            }
        });

        // Set onClickListener for rewind button
        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement rewind logic
            }
        });

        // Set onClickListener for forward button
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement forward logic
            }
        });

        // Set OnSeekBarChangeListener for playbackSeekBar
        playbackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Implement seek logic
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Implement logic when user starts tracking touch on seek bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Implement logic when user stops tracking touch on seek bar
            }
        });

        return view;
    }

    public void setSelectedFile(Uri selectedFileUri) {
        this.selectedFileUri = selectedFileUri;

        // TODO: Implement logic to handle the selected file URI in FragmentPlay
    }

}
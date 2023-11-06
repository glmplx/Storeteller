package com.example.storeteller.ui.library;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.databinding.FragmentLibraryBinding;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LibraryViewModel dashboardViewModel =
                new ViewModelProvider(this).get(LibraryViewModel.class);

        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLibrary;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Trouver le bouton par son ID
        Button button = getView().findViewById(R.id.button);

        // Ajouter un OnClickListener au bouton
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent target = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                    Toast.makeText(getActivity(), "bite", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
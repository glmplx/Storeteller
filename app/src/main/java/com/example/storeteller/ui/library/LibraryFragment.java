package com.example.storeteller.ui.library;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.SharedViewModel;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LibraryFragment extends Fragment {

    private static final int PICK_PDF_REQUEST = 1;
    private ListView fileList;
    private Button selectFileButton;
    private Button viewFileButon;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private Uri selectedFileUri;
    private ArrayAdapter<String> adapter;
    private HashMap<Uri, String> recentSelectedFile = new HashMap<>();
    private String selectedFileName;
    private SharedViewModel sharedViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        fileList = view.findViewById(R.id.fileList);
        selectFileButton = view.findViewById(R.id.selectFileButton);
        viewFileButon = view.findViewById(R.id.viewFileButon);

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>(recentSelectedFile.values()));
        fileList.setAdapter(adapter);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set onClickListener for the select file button
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open file picker intent
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");  // Restrict to PDF files
                startActivityForResult(intent, PICK_PDF_REQUEST);
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Move the code from onCreateView to onViewCreated
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    selectedFileUri = data.getData();
                }
            }
        });

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected file URI from the adapter
                selectedFileUri = (Uri) recentSelectedFile.keySet().toArray()[position];
                selectedFileName = getFileNameFromUri(selectedFileUri);
                sharedViewModel.setSelectedFileUri(selectedFileUri);
                String text = readPdfFile(selectedFileUri);
                sharedViewModel.setText(text);
                // Notify the listener (MainActivity) about the selected file
            }
        });

        viewFileButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileName != null) {
                    if(selectedFileUri != null){
                        selectedFileName = getFileNameFromUri(selectedFileUri);
                    }
                    // Afficher un toast avec le nom du fichier
                    Toast.makeText(getActivity(), "Nom du fichier sélectionné : " + selectedFileName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Aucun fichier sélectionné.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Get selected PDF file URI
                Uri newSelectedFileUri = data.getData();

                // Replace the selected file URI in the adapter and update the UI
                if (newSelectedFileUri != null) {
                    selectedFileUri = newSelectedFileUri;
                    selectedFileName = getFileNameFromUri(selectedFileUri);

                    // Add the selected file URI to the recentSelectedFile list
                    recentSelectedFile.put(selectedFileUri,selectedFileName);
                    adapter.clear();
                    adapter.addAll(recentSelectedFile.values());
                    adapter.notifyDataSetChanged();
                }
                // Set the selected file URI in the SharedViewModel
                sharedViewModel.setSelectedFileUri(selectedFileUri);
                String text = readPdfFile(selectedFileUri);
                sharedViewModel.setText(text);
            }
        }
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

    public String readPdfFile(Uri uri) {
        String stringParser = "";
        try {
            File file = new File(uri.getPath());
            PDDocument document = Loader.loadPDF(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(document.getNumberOfPages());
            stringParser = pdfStripper.getText(document);
        } catch (IOException e) {
            stringParser = "Problem with converter";
            e.printStackTrace();
        }
        return stringParser;
    }


}
package com.example.storeteller.ui.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
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
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LibraryFragment extends Fragment {

    private static final int PICK_PDF_REQUEST = 1;
    private ListView fileList;
    private Button viewFileButton;
    private Uri selectedFileUri;
    private ArrayAdapter<String> adapter;
    private HashMap<Uri, String> recentSelectedFile = new HashMap<>();
    private String selectedFileName;
    private SharedViewModel sharedViewModel;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor myPrefsEdit;
    private Map<String, ?> allEntries;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        fileList = view.findViewById(R.id.fileList);
        Button selectFileButton = view.findViewById(R.id.selectFileButton);
        viewFileButton = view.findViewById(R.id.viewFileButon);

        myPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE);
        myPrefsEdit = myPrefs.edit();


        allEntries = myPrefs.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String fileName = entry.getKey();
            String uriString = entry.getValue().toString();
            Log.d("fileName",fileName);
            Log.d("uriString",uriString);
            Uri fileUri = Uri.parse(uriString);
            recentSelectedFile.put(fileUri, fileName);
        }

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>(recentSelectedFile.values()));
        fileList.setAdapter(adapter);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open file picker intent
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent, PICK_PDF_REQUEST);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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
                selectedFileUri = (Uri) recentSelectedFile.keySet().toArray()[position];
                Log.d("setLoadedFileSelected","FileName : " + getFileNameFromUri(selectedFileUri) + " Uri : " + selectedFileUri.toString());
                selectedFileName = getFileNameFromUri(selectedFileUri);
                sharedViewModel.setSelectedFileUri(selectedFileUri);
                String text = readPdfFile(selectedFileUri);
                Log.d("text",text);
                sharedViewModel.setText(text);
            }
        });

        viewFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileName != null) {
                    if(selectedFileUri != null){
                        selectedFileName = getFileNameFromUri(selectedFileUri);
                    }
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

                Uri newSelectedFileUri = data.getData();

                if (newSelectedFileUri != null) {
                    selectedFileUri = newSelectedFileUri;
                    selectedFileName = getFileNameFromUri(selectedFileUri);

                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                    try {
                        getActivity().getContentResolver().takePersistableUriPermission(selectedFileUri, takeFlags);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    saveFileToCache(selectedFileUri,selectedFileName);

                    Log.d("setSavedFileSelected", "FileName: " + selectedFileName + ", Uri: " + selectedFileName);
                    myPrefsEdit.putString(selectedFileName, selectedFileUri.toString());
                    myPrefsEdit.apply();

                    recentSelectedFile.put(selectedFileUri,selectedFileName);

                    adapter.clear();
                    adapter.addAll(recentSelectedFile.values());
                    adapter.notifyDataSetChanged();
                }

                sharedViewModel.setSelectedFileUri(selectedFileUri);
                String text = readPdfFile(selectedFileUri);
                sharedViewModel.setText(text);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
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

    public String readPdfFile(Uri uri) {
        String text = "";
        try {
            File cachedFile = new File(requireContext().getCacheDir(), getFileNameFromUri(uri));
            InputStream fis = Files.newInputStream(cachedFile.toPath());
            PdfReader reader = new PdfReader(fis);
            int n = reader.getNumberOfPages();
            for (int i = 0; i <n ; i++) {
                text = text +(PdfTextExtractor.getTextFromPage(reader, i + 1).trim()) + ("\n");
            }
            reader.close();
        } catch (IOException e) {
            text = "Problem with converter";
        }
        return text;
    }

    private void saveFileToCache(Uri uri, String fileName) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);

                // Save the file in the cache directory
                File cacheDir = requireContext().getCacheDir();
                File outputFile = new File(cacheDir, fileName);

                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    outputStream.write(buffer);
                }

                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (selectedFileUri != null) {
            getActivity().getContentResolver().releasePersistableUriPermission(selectedFileUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

}
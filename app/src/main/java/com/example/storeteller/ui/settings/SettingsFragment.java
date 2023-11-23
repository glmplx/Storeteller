package com.example.storeteller.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.storeteller.R;
import com.example.storeteller.SharedViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private final HashMap<String, Locale> countryLocaleMap = new HashMap<>();
    private final TreeMap<String, Locale> sortedCountryLocaleMap = new TreeMap<>();
    private SharedViewModel sharedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Spinner spinner = view.findViewById(R.id.spinner);

        SeekBar pitchSeekBar = view.findViewById(R.id.seek_bar_pitch);

        SeekBar speedSeekBar = view.findViewById(R.id.seek_bar_speed);

        countryLocaleMap.put("ENGLISH", Locale.US);
        countryLocaleMap.put("FRANCE", Locale.FRANCE);
        countryLocaleMap.put("GERMAN", Locale.GERMAN);
        countryLocaleMap.put("ITALY", Locale.ITALY);
        countryLocaleMap.put("SPAIN", new Locale("es", "ES"));
        countryLocaleMap.put("RUSSIAN", new Locale("ru", "RU"));
        countryLocaleMap.put("POLISH", new Locale("pl", "PL"));
        countryLocaleMap.put("CHINA", Locale.CHINA);
        countryLocaleMap.put("ARABIC", new Locale("ar", "SA"));
        countryLocaleMap.put("HINDI", new Locale("hi", "IN"));
        countryLocaleMap.put("BENGALI", new Locale("bn", "BD"));
        countryLocaleMap.put("PORTUGUESE", new Locale("pt", "BR"));
        countryLocaleMap.put("INDONESIAN", new Locale("id", "ID"));
        countryLocaleMap.put("URDU", new Locale("ur", "PK"));
        countryLocaleMap.put("CANADIAN FRENCH", new Locale("fr", "CA"));
        countryLocaleMap.put("GERMAN SWITZERLAND", new Locale("de", "CH"));
        countryLocaleMap.put("DUTCH", new Locale("nl", "NL"));
        countryLocaleMap.put("TURKISH", new Locale("tr", "TR"));
        countryLocaleMap.put("VIETNAMESE", new Locale("vi", "VN"));
        countryLocaleMap.put("JAPANESE", Locale.JAPAN);
        countryLocaleMap.put("KOREAN", Locale.KOREA);
        countryLocaleMap.put("THAI", new Locale("th", "TH"));
        countryLocaleMap.put("MALAY", new Locale("ms", "MY"));
        countryLocaleMap.put("TAGALOG", new Locale("tl", "PH"));
        countryLocaleMap.put("UKRAINIAN", new Locale("uk", "UA"));
        countryLocaleMap.put("PERSIAN", new Locale("fa", "IR"));
        countryLocaleMap.put("SWEDISH", new Locale("sv", "SE"));
        countryLocaleMap.put("DANISH", new Locale("da", "DK"));
        countryLocaleMap.put("NORWEGIAN", new Locale("no", "NO"));
        countryLocaleMap.put("FINNISH", new Locale("fi", "FI"));
        countryLocaleMap.put("GREEK", new Locale("el", "GR"));

        sortedCountryLocaleMap.putAll(countryLocaleMap);

        float defaultPitch = 1.0f;
        float defaultSpeed = 1.0f;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, new ArrayList<>(sortedCountryLocaleMap.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        pitchSeekBar.setProgress((int) (defaultPitch * 100));
        speedSeekBar.setProgress((int) (defaultSpeed * 100));


        pitchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float pitch = convertProgressToValue(progress);
                sharedViewModel.setPitch(pitch);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = convertProgressToValue(progress);
                sharedViewModel.setSpeed(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCountry = parent.getItemAtPosition(position).toString();
        Locale selectedLocale = countryLocaleMap.get(selectedCountry);

        if (selectedLocale != null) {
            sharedViewModel.setSelectedLocale(selectedLocale);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private float convertProgressToValue(int progress) {
        return 0.5f + (float) progress / 200.0f;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
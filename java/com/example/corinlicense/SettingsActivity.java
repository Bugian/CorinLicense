package com.example.corinlicense;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.text.Text;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private SettingsNicknameManager nicknameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_settings);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        NavigationHandler navigationHandler = new NavigationHandler(this);
        nicknameManager = new SettingsNicknameManager(this, dbHelper);

        //Language Button
        LinearLayout languageLayout = findViewById(R.id.languageBtnContainer);
        languageLayout.setOnClickListener(v -> SettingsLanguageManager.showLanguageSelection(SettingsActivity.this));


        //Nickname Button
        LinearLayout nicknameLayout = findViewById(R.id.nicknameBtnContainer);
        nicknameLayout.setOnClickListener(v -> nicknameManager.showNicknameDialog());

        //Delete Button
        LinearLayout deleteLayout = findViewById(R.id.deleteDataBtnContainer);
        deleteLayout.setOnClickListener(v -> {
            dbHelper.deleteAllFinancialData();
            Toast.makeText(SettingsActivity.this, "Financial Data Has Been Deleted.", Toast.LENGTH_SHORT).show();
        });

        //Transaction History Button
        LinearLayout transactionHistoryLayout = findViewById(R.id.transactionHistoryBtn);
        transactionHistoryLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, TransactionHistory.class);
            startActivity(intent);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navigationHandler);



    }

    private void loadLocale() {
        // Obține preferințele salvate
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (!language.equals("")) {
            // Dacă există o limbă salvată, setează-o
            setLocale(language);
        }
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Salvează preferința de limbă
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", langCode);
        editor.apply();
    }

}



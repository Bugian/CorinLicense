package com.example.corinlicense;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class SettingsLanguageManager {

    public static void showLanguageSelection(final Activity activity) {
        final String[] languages = {"Română", "Français", "Deutsch", "English", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.choose_language)
                .setItems(languages, (dialog, which) -> {
                    String langCode = "";
                    switch (which) {
                        case 0:
                            langCode = "ro";
                            break;
                        case 1:
                            langCode = "fr";
                            break;
                        case 2:
                            langCode = "de";
                            break;
                        case 3:
                            langCode = "en";
                            break;
                        case 4:
                            dialog.dismiss();
                            break;
                    }
                    setLocale(activity, langCode);
                });
        builder.show();
    }

    public static void setLocale(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        activity.createConfigurationContext(config);

        // Salvează preferința de limbă
        SharedPreferences.Editor editor = activity.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", langCode);
        editor.apply();
    }

}

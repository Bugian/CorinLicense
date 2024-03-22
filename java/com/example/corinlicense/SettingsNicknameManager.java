package com.example.corinlicense;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SettingsNicknameManager {
    private final Context context;
    private final DatabaseHelper dbHelper;
    private NicknameManager nicknameManager;


    public SettingsNicknameManager(Context context, DatabaseHelper dbHelper){
        this.context = context;
        this.dbHelper = dbHelper;
        loadNickname();
    }
    private void loadNickname() {
        this.nicknameManager = dbHelper.getNickname();
        if (this.nicknameManager == null) {
            this.nicknameManager = new NicknameManager("Corin Coteata");
        }
    }

    private void updateDatabase(String nickname){
        nicknameManager = new NicknameManager(nickname);
        dbHelper.saveNickname(nickname);
    }
    public void showNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alege un Nickname");

        final EditText inputField = new EditText(context);
        builder.setView(inputField);

        builder.setPositiveButton("Salvează", (dialog, which) -> {
            String nickname = inputField.getText().toString();
            if (!nickname.trim().isEmpty()) {
                saveNickname(nickname);
            }
        });
        builder.setNegativeButton("Anulează", (dialog, which) -> dialog.cancel());

        builder.show();

    }

    private void saveNickname(String nickname) {
        try {
            updateDatabase(nickname);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getNickname() { return nicknameManager.getNickname(); }
}

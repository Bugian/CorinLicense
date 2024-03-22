package com.example.corinlicense;

import static com.example.corinlicense.NavigationHandler.REQUEST_IMAGE_CAPTURE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private TextView balanceCountView;
    private TextView spentTodayCountView;
    private TextView savingsCountView;
    private TextView nicknameTextView;
    private FinancialManager financialManager;
    private SettingsNicknameManager nicknameManager;
    private DialogManager dialogManager;
    private ImageView profileImageView;
    private ActivityResultLauncher<Intent> imageResultLauncher;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inițializare Views
        balanceCountView = findViewById(R.id.BalanceCount);
        spentTodayCountView = findViewById(R.id.spentTodayCount);
        savingsCountView = findViewById(R.id.SavingsCount);
        profileImageView = findViewById(R.id.missAvatarView);
        nicknameTextView = findViewById(R.id.missNicknameView);
        setupImageResultLauncher();

        //Setare eveniment Add
        LinearLayout addBtnContainer = findViewById(R.id.addBtnContainer);
        addBtnContainer.setOnClickListener(v -> dialogManager.showAddDialog(financialManager, amount -> updateViews())
        );

        //Setare eveniment Withdraw
        LinearLayout withdrawContainer = findViewById(R.id.WithdrawContainer);
        withdrawContainer.setOnClickListener(v -> dialogManager.showWithdrawDialog(financialManager, amount -> updateViews()));

        //Setare eveniment Savings
        LinearLayout savingsBtnContainer = findViewById(R.id.SavingsBtnContainer);
        savingsBtnContainer.setOnClickListener(v -> dialogManager.showSavingsDialog(financialManager, (success, message) -> {
            if (!success) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
            updateViews();
        }));


        DatabaseHelper dbHelper = new DatabaseHelper(this);
        financialManager = new FinancialManager(dbHelper);
        nicknameManager = new SettingsNicknameManager(context, dbHelper);

        NavigationHandler navigationHandler = new NavigationHandler(this);
        navigationHandler.updateActivity(this);

        dialogManager = new DialogManager(this);
        profileImageView.setOnClickListener(v -> ImageHandler.selectImageFromGalleryOrCamera(this, imageResultLauncher));





        //Setare Data curenta
        TextView dateText = findViewById(R.id.dateText);
        String currentDate = getCurrentDate();
        dateText.setText(currentDate);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navigationHandler);

        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (!language.equals("")) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        updateViews();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void updateViews() {
        balanceCountView.setText(formatCurrency(financialManager.getBalance()));
        spentTodayCountView.setText(formatCurrency(financialManager.getSpentToday()));
        savingsCountView.setText(formatCurrency(financialManager.getSavings()));
        nicknameTextView.setText(nicknameManager.getNickname());
    }

    private String formatCurrency(BigDecimal value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }


    private void setupImageResultLauncher() {
        imageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        Bitmap bitmap;
                        try {
                            Uri imageUri = null;
                            if (data.getData() != null) {
                                // Imagine selectată din galerie
                                imageUri = data.getData();
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                                Toast.makeText(this, getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
                            } else {
                                // Imagine capturată de cameră
                                Bundle extras = data.getExtras();
                                if (extras != null && extras.containsKey("data")) {
                                    bitmap = (Bitmap) extras.get("data");
                                    Toast.makeText(this, getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, getString(R.string.error_capture), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            if (bitmap != null) {
                                // Procesez imaginea și o afișez
                                String imagePath = imageUri != null ? imageUri.getPath() : null;
                                processImage(bitmap, imagePath, 100);
                            } else {
                                profileImageView.setImageResource(R.drawable.missing_avatar);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, getString(R.string.error_processing), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
    public void processImage(Bitmap bitmap, String imagePath, int pixels) {
        int rotationAngle = ImageHandler.getRotationAngle(imagePath);
        Bitmap rotatedBitmap = ImageHandler.rotateBitmap(bitmap, rotationAngle);
        Bitmap roundedBitmap = ImageHandler.getRoundedCornerBitmap(rotatedBitmap, pixels);
        profileImageView.setImageBitmap(roundedBitmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            OCRProcessor ocrProcessor = new OCRProcessor();
            ocrProcessor.processImageAndExtractCashValue(imageBitmap, this, new OCRProcessor.TextExtractedListener() {
                @Override
                public void onTextExtracted(String extractedText) {
                    if (extractedText != null) {
                        try {
                            BigDecimal cashValue = new BigDecimal(extractedText);
                            boolean withdrawalSuccess = financialManager.withdrawFromBalance(cashValue);
                            if (withdrawalSuccess) {
                                BigDecimal balance = financialManager.getBalance();
                                BigDecimal spentToday = financialManager.getSpentToday();
                                balanceCountView.setText(balance.toPlainString());
                                spentTodayCountView.setText(spentToday.toPlainString());
                            } else {
                                Toast.makeText(MainActivity.this, "Eroare: Fonduri insuficiente", Toast.LENGTH_LONG).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Eroare: Formatul numărului nu este valid.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Eroare: Valoarea nu a putut fi determinată.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(MainActivity.this, "Eroare la procesarea OCR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}


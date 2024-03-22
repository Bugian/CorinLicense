package com.example.corinlicense;


import android.Manifest;
import android.app.Activity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.TextView;
import android.widget.Toast;

import static java.lang.String.format;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE_GALLERY = 2;
    private double spentToday = 0.0;
    private final ActivityResultLauncher<Intent> someActivityResultLauncher;
    private double currentBalance = 0;
    public MainActivity() {
        this.someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verificarea și solicitarea permisiunilor pentru cameră
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permisiunea pentru cameră nu a fost acordată, așa că o solicităm
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v -> showAddAmountDialog());

        Button withdrawBtn = findViewById(R.id.Withdraw);
        withdrawBtn.setOnClickListener(v -> showWithdrawAmountDialog());


            // Update the balance TextView
            TextView balanceCountTextView = findViewById(R.id.BalanceCount);
        balanceCountTextView.setText(format(Locale.getDefault(), "%.2f", currentBalance));

            // Log a message (for testing purposes)
            Log.d("BUTTONS", "User tapped the Add button");
        }




    private void showWithdrawAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Withdraw");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Withdraw", (dialog, which) -> {
            String amountStr = input.getText().toString();
            if (!amountStr.isEmpty()) {
                double withdrawnAmount = Double.parseDouble(amountStr);
                updateBalance(withdrawnAmount);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showAddAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Money");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String amountStr = input.getText().toString();
            if (!amountStr.isEmpty()) {
                double addedAmount = Double.parseDouble(amountStr);
                updateBalance(addedAmount);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateBalance(double addedAmount) {
        currentBalance += addedAmount;

        // Update the balance TextView
        TextView balanceCountTextView = findViewById(R.id.BalanceCount);
        balanceCountTextView.setText(String.format(Locale.getDefault(), "%.2f RON", currentBalance));

        // Log a message (for testing purposes)
        Log.d("BUTTONS", "User added: " + addedAmount + " to the balance");
    }

    private void handleActivityResult(ActivityResult result) {
        ImageView imageView = findViewById(R.id.imageView);

        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Afișează imaginea selectată din galerie în ImageView
                    imageView.setImageURI(selectedImageUri);
                } else {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        for (String key : extras.keySet()) {
                            Object value = extras.get(key);
                            if (value != null) {
                                Log.d("EXTRAS", key + " : " + value.toString());
                            } else {
                                Log.d("EXTRAS", key + " : null");
                            }
                        }
                    }
                }
            }
        }
    }

    public void selectImageFromGalleryOrCamera(View view) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    someActivityResultLauncher.launch(takePictureIntent);
                }
            } else if (items[item].equals("Choose from Library")) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(pickPhotoIntent);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void withdrawBalance(double withdrawnAmount) {
        currentBalance -= withdrawnAmount;
        spentToday += withdrawnAmount;

        // Update the balance TextView
        TextView balanceCountTextView = findViewById(R.id.BalanceCount);
        balanceCountTextView.setText(String.format(Locale.getDefault(), "%.2f RON", currentBalance));

        // Log a message (for testing purposes)
        Log.d("BUTTONS", "User withdrawn: " + withdrawnAmount + " from balance");
    }
    public void withdraw(double amount) {
        if (amount <= currentBalance) {
            currentBalance -= amount;
            spentToday += amount;

            updateBalance(); // Actualizare vizuală a soldului și cheltuielii de astăzi

            // Log pentru retrageri (opțional)
            Log.d("WITHDRAW", "User withdrew: " + amount);
        } else {
            // Afișare mesaj pentru resurse insuficiente
            Toast.makeText(this, "Insufficient Resources!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView imageView = findViewById(R.id.imageView);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap imageBitmap = null;
                if (data != null){
                    Bundle extras = data.getExtras();
                    if (extras != null && extras.containsKey("data")){
                        imageBitmap = (Bitmap) extras.get("data");
                    }
                }
                if (imageBitmap != null){
                    imageView.setImageBitmap(imageBitmap);
                } else {
                    imageView.setImageResource(R.mipmap.placeholder_image);
                }
                imageView.setImageBitmap(imageBitmap);
            } else if (requestCode == PICK_IMAGE_GALLERY) {
                Uri selectedImageUri = data.getData();
                imageView.setImageURI(selectedImageUri);
            }
        }
    }


}


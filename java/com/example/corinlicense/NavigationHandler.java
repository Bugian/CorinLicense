package com.example.corinlicense;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHandler implements BottomNavigationView.OnItemSelectedListener {

    private Activity activity;
    private ActivityResultLauncher<Intent> settingsResultLauncher;
    private final Context context;
    public static final int REQUEST_IMAGE_CAPTURE = 1; // Cod de solicitare pentru captura de imagine

    public NavigationHandler(Activity activity, ActivityResultLauncher<Intent> settingsResultLauncher, Context context) {
        this.activity = activity;
        this.settingsResultLauncher = settingsResultLauncher;
        this.context = context;
    }

    public void updateActivity(Activity activity) {
        this.activity = activity;
    }

    public NavigationHandler(Context context) {
        this.context = context;
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navigation_home) {
            context.startActivity(new Intent(context, MainActivity.class));
            return true;
        } else if (id == R.id.navigation_camera) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                if (activity != null) {
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(context, "Eroare: Nu se poate accesa camera în acest moment.", Toast.LENGTH_LONG).show();
                }
            }
            return true;
    } else if (id == R.id.navigation_settings) {
            if (context != null) {
                context.startActivity(new Intent(context, SettingsActivity.class));
            } else if (activity != null && settingsResultLauncher != null) {
                Intent intent = new Intent(activity, SettingsActivity.class);
                settingsResultLauncher.launch(intent);
            }
            return true;
        }
        return false;
    }
}

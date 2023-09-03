package com.bishal.watch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PermissionActivity extends AppCompatActivity {

    // Buttons to ask different types of permissions
    Button p1, p2, p3;

    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> requestSystemAlertLauncher;

    private boolean p1PermissionGranted = false;
    private boolean p2PermissionGranted = false;
    private boolean p3PermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        p1 = findViewById(R.id.permission1);
        p2 = findViewById(R.id.permission2);
        p3 = findViewById(R.id.permission3);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    for (String permission : permissions.keySet()) {
                        if (!permissions.get(permission)) {
                            // Permission denied, handle accordingly
                            Toast.makeText(this, "Permission Denied: " + permission, Toast.LENGTH_SHORT).show();
                        } else {
                            // Permission granted, handle accordingly
                            if (permission.equals(Manifest.permission.CAMERA)) {
                                p1PermissionGranted = true;
                                p1.setEnabled(false);
                                p2.setEnabled(true);
                            } else if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                                p2PermissionGranted = true;
                                p2.setEnabled(false);
                                p3.setEnabled(true);
                            } else if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                p3PermissionGranted = true;
                                p3.setEnabled(false);
                                Toast.makeText(this, "All permissions Granted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        requestSystemAlertLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                        // Permission granted, handle accordingly
                        p2PermissionGranted = true;
                        p2.setEnabled(false);
                        p3.setEnabled(true);
                    } else {
                        // Permission denied or not granted, handle accordingly
                        Toast.makeText(this, "SystemAlert Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                });

        p1.setOnClickListener(v -> openPermissionActivity(1, "Camera Permission"));
        p2.setOnClickListener(v -> openPermissionActivity(2, "SystemAlert Permission"));
        p3.setOnClickListener(v -> openPermissionActivity(3, "External Storage Permission"));
    }

    private void openPermissionActivity(int permission_type, String message) {
        switch (permission_type) {
            case 1:
                requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
                break;
            case 2:
                if (p1PermissionGranted) {
                    Intent systemAlertIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    requestSystemAlertLauncher.launch(systemAlertIntent);
                } else {
                    Toast.makeText(this, "Please grant Camera permission first", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (p2PermissionGranted) {
                    requestPermissionLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                } else {
                    Toast.makeText(this, "Please grant SystemAlert permission first", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}

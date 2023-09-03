package com.bishal.watch;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    private Camera camera;
    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user) {
        super.onPasswordFailed(context, intent, user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Intent serviceIntent = new Intent(context, ImageCaptureService.class);
//            ContextCompat.startForegroundService(context, serviceIntent);

            // Pass the Application context to ImageCaptureHelper
//            Intent serviceIntent = new Intent(context, ImageCaptureService.class);
//            ContextCompat.startForegroundService(context, serviceIntent);

// my initial code
            Intent serviceIntent = new Intent(context, CameraActivity.class);
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);

        }


    }


}

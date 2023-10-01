package com.bishal.watch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private ComponentName deviceAdminReceiver;
    private DevicePolicyManager devicePolicyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminReceiver = new ComponentName(this, MyDeviceAdminReceiver.class);
//
//
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               if (allPermissionsGranted() && devicePolicyManager.isAdminActive(deviceAdminReceiver)){
                   startActivity(new Intent(SplashActivity.this,MainActivity.class));
               }else{
                   startActivity(new Intent(SplashActivity.this,PermissionActivity.class));
               }
            }
        },2000);
    }
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}

package com.bishal.watch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AutomaticZenRule;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PermissionActivity extends AppCompatActivity {

    // Buttons to ask different types of permissions
    Button p1, p2, p3,p4,p5;

    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private  ActivityResultLauncher<Intent> requestSystemAlertLauncher;
    private ActivityResultLauncher<Intent> requestDeviceAdminLauncher;
    private ImageView i1,i2,i3,i4,i5;



    private boolean p1PermissionGranted = false;
    private boolean p2PermissionGranted = false;
    private boolean p3PermissionGranted= false;
    private boolean p4PermissionGranted= false;
    private boolean p5PermissionGranted= false;
    private boolean p6PermissionGranted = false;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdminReceiver;
    private String TAG="Permission check";
    private Button p6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        p1 = findViewById(R.id.device_permission);
        p2 = findViewById(R.id.system_alert_permission);
        p3 = findViewById(R.id.cam_permission);
        p4 = findViewById(R.id.storage_permission);
        p5 = findViewById(R.id.location_permission);
        p6=findViewById(R.id.permission_ok_btn);
        i1=findViewById(R.id.i1);
        i2=findViewById(R.id.i2);
        i3=findViewById(R.id.i3);
        i4=findViewById(R.id.i4);
        i5=findViewById(R.id.i5);

        setInitialButtonVisibility();
        //init device andmin
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminReceiver = new ComponentName(this, MyDeviceAdminReceiver.class);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    for (String permission : permissions.keySet()) {
                        if (Boolean.FALSE.equals(permissions.get(permission))) {
                            // Permission denied, handle accordingly
                            Toast.makeText(this, "Permission Denied: " + permission, Toast.LENGTH_SHORT).show();
                        } else {
                            // Permission granted, handle accordingly
                            if (permission.equals(Manifest.permission.CAMERA)) {

                               changeButtonVisibility(3);
                                Log.d(TAG, "onCreate: camera per");
                            } if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                changeButtonVisibility(4);
                                Log.d(TAG, "onCreate: External sfksdlk");
                            }
                            if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)
                                    ||permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                                changeButtonVisibility(5);
                                Log.d(TAG, "onCreate: edternal fdlsj");
//                                Toast.makeText(this, "All permissions Granted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        requestSystemAlertLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "alert window: "+result.getResultCode());
                        changeButtonVisibility(2);

                });
        requestDeviceAdminLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode()==RESULT_OK){
                        changeButtonVisibility(1);
                    }else{
                        Toast.makeText(this, "We Need this to check wrong password attempt", Toast.LENGTH_LONG).show();
                    }
                });
        p1.setOnClickListener(v -> openPermissionActivity(1, "Device Permission"));
        p2.setOnClickListener(v -> openPermissionActivity(2, "System Alert Permission"));
        p3.setOnClickListener(v -> openPermissionActivity(3, "Camera Permission"));
        p4.setOnClickListener(v -> openPermissionActivity(4, "Storage Permission"));
        p5.setOnClickListener(v -> openPermissionActivity(5, "Location Permission"));
        p6.setOnClickListener(v -> openPermissionActivity(6, "Final Button"));
    }

    private void setInitialButtonVisibility() {
        p2.setPressed(true);
        p3.setPressed(true);
        p4.setPressed(true);
        p5.setPressed(true);
        p6.setPressed(true);

    }

    private void changeButtonVisibility(int button) {
        switch (button){
            case 1:
                p1PermissionGranted = true;
                p2.setPressed(false);
                p2.setEnabled(true);
                p1.setEnabled(false);
                i1.setImageResource(R.drawable.tick);
                break;
            case 2:
                p2PermissionGranted = true;
                p3.setPressed(false);
                p3.setEnabled(true);
                p2.setEnabled(false);
                i2.setImageResource(R.drawable.tick);
                break;
            case 3:
                p3PermissionGranted = true;
                p4.setPressed(false);
                p4.setEnabled(true);
                p3.setEnabled(false);
                i3.setImageResource(R.drawable.tick);

                break;
            case 4:
                p4PermissionGranted = true;
                p5.setPressed(false);
                p5.setEnabled(true);
                p4.setEnabled(false);
                i4.setImageResource(R.drawable.tick);

                break;

            case 5:
                p5PermissionGranted=true;
                p6.setPressed(false);
                p6.setEnabled(true);
                p5.setEnabled(false);
                i5.setImageResource(R.drawable.tick);

                break;
            case 6:
                p6PermissionGranted=true;
                //this permissionis required for the the
            default:
                break;

        }
    }


    private void openPermissionActivity(int permission_type, String message) {
        switch (permission_type) {
            case 1:
                Intent device_intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                device_intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiver);
                device_intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Permission needed to detect wrong password attempt");
                requestDeviceAdminLauncher.launch(device_intent);
                break;
            case 2:
                if (p1PermissionGranted) {
                    applyMiuiPermission(PermissionActivity.this);
                } else {
                    Toast.makeText(this, "Please grant device admin permission first", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (p2PermissionGranted) {
                    requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
                } else {
                    Toast.makeText(this, "Please grant SystemAlert permission first", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if(p3PermissionGranted){
                    requestPermissionLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
                else {
                    Toast.makeText(this, "Please grant Camera permission first", Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                if(p4PermissionGranted){
                    requestPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION});
                }
                else {
                    Toast.makeText(this, "Please grant Storage permission first", Toast.LENGTH_SHORT).show();
                }
                break;
            case 6:
                if(p5PermissionGranted){
                    Intent intent=new Intent(PermissionActivity.this, MainActivity.class);
                    startActivity(intent);
                    //final check and launch main

                }
                else {
                    Toast.makeText(this, "Please grant Location permission first", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public  void applyMiuiPermission(Context context) {
        int versionCode = getMiuiVersion();
        if (versionCode == 5) {
            goToMiuiPermissionActivity_V5(context);
        } else {
            goToMiuiPermissionActivity_Other(context);
//            Intent systemAlertIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                    requestSystemAlertLauncher.launch(systemAlertIntent);
            Toast.makeText(context, "give other required permission manually", Toast.LENGTH_LONG).show();
//            Log.e(TAG, "this is a special MIUI rom version, its version code " + versionCode);
        }
    }



    public static int getMiuiVersion() {
        String version = RomUtils.getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
//                Log.e(TAG, "get miui version code error, version : " + version);
            }
        }
        return -1;
    }

    public static void goToMiuiPermissionActivity_V5(Context context) {
        Intent intent = null;
        String packageName = context.getPackageName();
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package" , packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
//        if (isIntentAvailable(intent, context)) {
//            context.startActivity(intent);
//        } else {
//            Log.e(TAG, "intent is not available!");
//        }
    }


    public  void goToMiuiPermissionActivity_Other (Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//
        PackageManager packageManager=this.getPackageManager();

        if (intent.resolveActivity(packageManager)!=null) {
            requestSystemAlertLauncher.launch(intent);
        } else {
            Intent intent2 = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            requestSystemAlertLauncher.launch(intent2);
            Log.e(TAG, "Intent is not available!");
        }
    }



}



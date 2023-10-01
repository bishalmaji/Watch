package com.bishal.watch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private Executor executor;
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    PreviewView mPreviewView;

    private String TAG = "test_camera";


    private FusedLocationProviderClient locationClient;
    private CancellationTokenSource canclecTask = new CancellationTokenSource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executor = Executors.newSingleThreadExecutor();
        showWhenLocked();


        setContentView(R.layout.activity_camera);


        mPreviewView = findViewById(R.id.camera);
        findViewById(R.id.camera_layout).setVisibility(View.INVISIBLE);

        if (allPermissionsGranted()) {

            Log.d(TAG, "onCreate: after start");
            getLocationData();
            startCamera();




        } else {
            Log.d(TAG, "onCreate: all permission not granted");
        }
    }

    private void getLocationData() {
        locationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task<Location> currentLocation = locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, canclecTask.getToken());
       currentLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
           @Override
           public void onComplete(@NonNull Task<Location> task) {
               if (task.isSuccessful())
               {
                   Location location=task.getResult();
              String latitude = String.valueOf(location.getLatitude());
              String longitude = String.valueOf(location.getLongitude());
               updateText(latitude,longitude);
                   Log.d(TAG, "onComplete: "+location);
               }
           }
       });
    }



    private void updateText(String latitude, String longitude) {

        try{
            SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a",Locale.getDefault());
            String formattedTime=sdf.format(new Date());

            String textToWrite="["+ latitude +" , "+ longitude +"]/"+ formattedTime;
            String timestamp=mDateFormat.format(new Date());
            File textFile=new File(getExternalFilesDir("/").getAbsolutePath(),"hello_"+timestamp+".txt");
            FileWriter writer=new FileWriter(textFile,false);
            writer.write(textToWrite);
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showWhenLocked() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
    }

    private void takeAndSavePic(ImageCapture imageCapture, ImageCapture.OutputFileOptions outputFileOptions) {
        imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.d(TAG, "run:successssssssssssssssss " + outputFileResults.getSavedUri());
//

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish(); // Finish the activity on the UI thread

                    }
                });
            }


            @Override
            public void onError(@NonNull ImageCaptureException error) {
                Log.d(TAG, "onError: " + error);
                error.printStackTrace();
            }
        });
    }
    String formattedTime;


    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();
        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageCapture);

         mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        File file = new File(getExternalFilesDir("/").getAbsolutePath(), mDateFormat.format(new Date()) + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        takeAndSavePic(imageCapture, outputFileOptions);
    }
    SimpleDateFormat mDateFormat;


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
}

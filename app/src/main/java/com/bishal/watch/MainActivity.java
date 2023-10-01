package com.bishal.watch;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DEVICE_ADMIN_PERMISSION_REQUEST_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 1002;

    DevicePolicyManager devicePolicyManager;
    ComponentName deviceAdminReceiver;
    private RecyclerView recyclerView;
    private List<Bitmap> imageList;
    private List<String> textList;
    private ImageListAdapter adapter;
    private int currentPermissionIndex = 0;
    Pair<ArrayList<String>, ArrayList<String>> filePathsPair ;
    ArrayList<String> imageFilePaths ;
    ArrayList<String> textFilePaths ;
    TextView no_result;

private String readFileContent(File file) {
    StringBuilder fileContent = new StringBuilder();

    try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            fileContent.append(line).append("\n");
        }

        br.close();
    } catch (IOException e) {
        e.printStackTrace();
    }

    return fileContent.toString();
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        no_result=findViewById(R.id.no_result);
        recyclerView = findViewById(R.id.listView);
        imageList = new ArrayList<>(); // Your list of Bitmaps
        textList=new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);


        filePathsPair=getImageAndTextFilePaths();
        imageFilePaths = filePathsPair.first;
        textFilePaths = filePathsPair.second;
        setData();
        adapter=new ImageListAdapter(MainActivity.this,imageList,textList);
        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }

    private void setData() {
    if(imageFilePaths.size() == 0){
        no_result.setVisibility(View.VISIBLE);
        return;
    }
    if(!imageList.isEmpty()){
        imageList.clear();
    }
        if(!textList.isEmpty()){
            textList.clear();
        }

        for (String imagePath:imageFilePaths) {

            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            imageList.add(bitmap);
        }

        for (String textPath:textFilePaths) {
            String content=readFileContent(new File(textPath));
            textList.add(content);
        }
        int is=imageList.size()-1;
        int ts=textList.size()-1;

        if(is!=ts){
            if(is>ts){
               for(int i=is;i>ts;i--){
                   imageList.remove(i);
               }
            }else{
                for(int i=ts;i>is;i--){
                    textList.remove(i);
                }
            }
        }
//        Log.d("TAG", "setData: "+textList.get(0));
    }

    private Pair<ArrayList<String>, ArrayList<String>> getImageAndTextFilePaths() {
        ArrayList<String> imageFilePaths = new ArrayList<>();
        ArrayList<String> textFilePaths = new ArrayList<>();

        File imagesDirectory = new File(getExternalFilesDir("/").getAbsolutePath());

        if (imagesDirectory.exists() && imagesDirectory.isDirectory()) {
            File[] files = imagesDirectory.listFiles();

            if (files != null) {
                for (File file : files) {
                    String filePath = file.getAbsolutePath();
                    String fileName = file.getName();

                    if (fileName.endsWith(".jpg")) {
                        imageFilePaths.add(filePath);
                    } else if (fileName.endsWith(".txt")) {
                        textFilePaths.add(filePath);
                    }
                }
            }
        }

        return new Pair<>(imageFilePaths, textFilePaths);
    }

    private void requestDeviceAdminPermission() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiver);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "fasfasdfs");
        startActivityForResult(intent, DEVICE_ADMIN_PERMISSION_REQUEST_CODE);
    }


}



package com.apps6283.shorts_app_sql_php_api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayList<DataHandler> dataHandlers = new ArrayList<>();
    ViewPager2 viewPager2;
    FirebaseDatabase database;
    boolean granted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();





        if (permissionCheck()){



            SharedPreferences preferences = getSharedPreferences("app", MODE_PRIVATE);
            String uri = preferences.getString("uri", "no");


            DocumentFile fileDoc = DocumentFile.fromTreeUri(MainActivity.this, Uri.parse(uri));





            DatabaseReference mRef = database.getReference("Videos");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot s : snapshot.getChildren()
                        ) {

                            Map map = (Map) s.getValue();

                            DataHandler data = new DataHandler(map.get("title").toString(), map.get("url").toString());
                            dataHandlers.add(data);

                            viewPager2 = findViewById(R.id.viewPager);


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                                PagerAdapter pagerAdapter = new PagerAdapter(dataHandlers, MainActivity.this,fileDoc);
                                viewPager2.setAdapter(pagerAdapter);

                            }else {

                                PagerAdapter pagerAdapter = new PagerAdapter(dataHandlers, MainActivity.this, null);
                                viewPager2.setAdapter(pagerAdapter);
                            }
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }else {


            Toast.makeText(this, "Please Give Permission to access Storage!", Toast.LENGTH_SHORT).show();
        }
     


    }



    private boolean permissionCheck() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

//            android 11 and up


            SharedPreferences preferences = getSharedPreferences("app", MODE_PRIVATE);
            String uri = preferences.getString("uri", "no");


            if (uri.matches("no")){

                granted = false;
                openDirectory();


            }else {
                
                
                granted = true;
                
            }






        } else {

            // less than android 11

            Dexter.withContext(MainActivity.this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()){

                        //if granted
                        granted = true;


                    }else {

                        granted = false;

                    }


                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                }
            });


        }


        return granted;


    }




    ////// paid /// paid  paid //////
    public void openDirectory() {

        String path = Environment.getExternalStorageDirectory() + "/DCIM";
        File file = new File(path);
        String startDir = null, secondDir, finalDirPath;

        if (file.exists()) {
            startDir = "DCIM";  // use %2F for "/"
        }

        StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        Intent intent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();

            Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");

            String scheme = uri.toString();

            Log.d("TAG", "INITIAL_URI scheme: " + scheme);

            scheme = scheme.replace("/root/", "/document/");

            finalDirPath = scheme + "%3A" + startDir;

            uri = Uri.parse(finalDirPath);

            intent.putExtra("android.provider.extra.INITIAL_URI", uri);

            Log.d("TAG", "uri: " + uri.toString());

            try {
                startActivityForResult(intent, 1234);
            } catch (ActivityNotFoundException ignored) {

            }}

    }

    @SuppressLint("NewApi")
    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234 && resultCode == RESULT_OK) {

            Uri treeUri = data.getData();


            getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION );


            SharedPreferences preferences = getSharedPreferences("app", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("uri", String.valueOf(treeUri));
            editor.apply();




            DocumentFile fileDoc = DocumentFile.fromTreeUri(MainActivity.this, treeUri);



            DatabaseReference mRef = database.getReference("Videos");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot s : snapshot.getChildren()
                        ) {

                            Map map = (Map) s.getValue();

                            DataHandler data = new DataHandler(map.get("title").toString(), map.get("url").toString());
                            dataHandlers.add(data);

                            viewPager2 = findViewById(R.id.viewPager);
                            PagerAdapter pagerAdapter = new PagerAdapter(dataHandlers, MainActivity.this,fileDoc);
                            viewPager2.setAdapter(pagerAdapter);

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });







        }
    }
}
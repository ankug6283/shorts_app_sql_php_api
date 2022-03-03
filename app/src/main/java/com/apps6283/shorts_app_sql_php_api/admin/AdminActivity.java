package com.apps6283.shorts_app_sql_php_api.admin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apps6283.shorts_app_sql_php_api.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {


    ImageView selectBtn;
    EditText titleET;
    Uri uri;

    String encodedVideo;

    String insertUrl = "https://freeapiabhishekg.000webhostapp.com/insert.php";
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


         progressDialog = new ProgressDialog(this);
         progressDialog.setMessage("Uploading..");
         progressDialog.setCancelable(false);





        selectBtn = findViewById(R.id.imageView2);
        titleET = findViewById(R.id.editTextTextPersonName);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Dexter.withContext(AdminActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,1234);



                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {



                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                permissionToken.continuePermissionRequest();

                            }
                        }).check();




            }
        });

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!titleET.getText().toString().isEmpty()){

                    if (!encodedVideo.isEmpty()){


                        uploadData(encodedVideo);


                    }else {
                        Toast.makeText(AdminActivity.this, "Select Video To Upload", Toast.LENGTH_SHORT).show();
                    }



                }else {

                    titleET.setError("Enter Title");
                }





            }
        });

    }

    private void uploadData(String encodedVideo) {


        progressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(AdminActivity.this, response, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

                titleET.setText("");
                uri = null;


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                progressDialog.dismiss();
                Toast.makeText(AdminActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        }){

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new HashMap<>();
                map.put("video",encodedVideo);
                map.put("title",titleET.getText().toString());


                return map;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234 && resultCode == RESULT_OK){

            if (data!=null){


                uri  = data.getData();

                encodedVideo = android.util.Base64.encodeToString(UploadHelper.getFileDataFromDrawable(AdminActivity.this,uri),Base64.DEFAULT);



            }



        }



    }


}
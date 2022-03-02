package com.apps6283.shorts_app_sql_php_api.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.apps6283.shorts_app_sql_php_api.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {


    ImageView selectBtn;
    EditText titleET;

    Uri uri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();



        selectBtn = findViewById(R.id.imageView2);
        titleET = findViewById(R.id.editTextTextPersonName);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent , 123);


            }
        });

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!titleET.getText().toString().isEmpty()){

                    if (uri!=null){

                        StorageReference uploadRef = reference.child(titleET.getText().toString()+".mp4");
                        uploadRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                uploadRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        FirebaseDatabase mdata = FirebaseDatabase.getInstance();
                                        DatabaseReference mRef = mdata.getReference("Videos");

                                        Map<String,Object> map = new HashMap<String,Object>();
                                        map.put("title",titleET.getText().toString());
                                        map.put("url",uri.toString());


                                        mRef.child(String.valueOf(System.currentTimeMillis())).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {


                                                Toast.makeText(AdminActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                            }

                                        });




                                    }
                                });


                            }


                        });


                    }else{
                        Toast.makeText(AdminActivity.this, "Select Video To upload", Toast.LENGTH_SHORT).show();
                    }


                }else {
                    titleET.setError("Enter Title");
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK){

            if (data!=null){


                uri  = data.getData();

                Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();


            }



        }



    }
}
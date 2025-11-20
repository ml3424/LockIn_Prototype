package com.example.lockin_prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {

    public static final int SELECT_IMAGE_CODE = 50; // intent request code
    public static final String STORAGE_FILE_NAME = "alpha_test_image.jpg";

    public ImageView iVGalleryPic;

    public StorageReference refStorage; // firebase-storage reference

    // holds the uri of the selected file locally
    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        iVGalleryPic = findViewById(R.id.iVGalleryPic);

        // get the firebase storage root reference
        refStorage = FirebaseStorage.getInstance().getReference();
    }

    // as shown in slide 30
    public void openFileChooser() {
        Intent intent = new Intent(); // intent for the image
        intent.setType("image/*"); // select image files
        intent.setAction(Intent.ACTION_GET_CONTENT); // get image from gallery

        startActivityForResult(intent, SELECT_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the result is from the request and if selection was successful
        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData(); // save uri
            iVGalleryPic.setImageURI(imageUri);

            uploadFile(imageUri); // upload the image to the firebase
        }
    }

    public void uploadFile(Uri uri) {
        if (uri != null) {
            final ProgressDialog pd = ProgressDialog.show(
                    this,
                    "Uploading Image",
                    "Please wait...",
                    true);

            // reference to the file(image) path in storage
            StorageReference fileRef = refStorage.child("images/" + STORAGE_FILE_NAME);

            // put the file (upload it)
            fileRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(this, "upload to storage successful!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void downloadFile() {
        // reference to the file to download
        StorageReference fileRef = refStorage.child("images/" + STORAGE_FILE_NAME);

        try {
            File localFile = File.createTempFile("tempImage", ".jpg");

            fileRef.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                        if (bitmap != null) {
                            iVGalleryPic.setImageBitmap(bitmap);
                            Toast.makeText(this, "download succeeded!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "failed decoding file", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "download failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "local file creation failed", Toast.LENGTH_LONG).show();
        }
    }

    public void onPickPic(View view) {
        openFileChooser();
    }

    public void onSavePic(View view) {
        downloadFile();
    }


}
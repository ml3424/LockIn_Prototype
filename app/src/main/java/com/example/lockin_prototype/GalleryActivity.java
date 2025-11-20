package com.example.lockin_prototype;

import android.content.Intent;
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
            // reference to the file(image) path in storage
            StorageReference fileRef = refStorage.child("images/" + STORAGE_FILE_NAME);

            // put the file (upload it)
            fileRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(this, "upload to storage successful!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        // show upload progress in the logcat(in the mean time)
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        Log.d("GalleryActivity", "upload progress: " + progress + "%");
                    });
        }
    }

    public void downloadFile() {
        // reference to the file to download
        StorageReference fileRef = refStorage.child("images/" + STORAGE_FILE_NAME);

        fileRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    // now load the actual http(s) url with Glide
                    Glide.with(this)
                            .load(uri) // uri is a standard https:// URL
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_delete)
                            .into(iVGalleryPic);
                    Toast.makeText(this, "download succeeded, loading image...", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "failed to get download url: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public void onPickPic(View view) {
        openFileChooser();
    }

    public void onSavePic(View view) {
        downloadFile();
    }


}
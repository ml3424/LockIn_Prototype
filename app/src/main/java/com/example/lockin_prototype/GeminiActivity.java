package com.example.lockin_prototype;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;


public class GeminiActivity extends AppCompatActivity {

    private ImageView iVPhoto;
    private TextView tVDescription;
    private String currentPath;
    private Bitmap imageBitmap;
    private GeminiManager geminiManager;

    private final String TAG = "PhotoActivity";

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 102;
    private static final int REQUEST_FULL_IMAGE_CAPTURE = 202;
    private static final int REQUEST_CHOOSER = 203;
    private static final String PHOTO_PROMPT = "תן תיאור קצר של התמונה, במידה ולא ברור מה יש בתמונה- תן הנחיה להביא תמונה אחרת או לצלם שוב.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gemini);

        iVPhoto = findViewById(R.id.iVPhoto);
        tVDescription = findViewById(R.id.tVDescription);

        geminiManager = GeminiManager.getInstance();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "External storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data_back) {
        super.onActivityResult(requestCode, resultCode, data_back);

        // only proceed if the result is ok
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // handle results from the chooser or direct camera capture
        if (requestCode == REQUEST_CHOOSER || requestCode == REQUEST_FULL_IMAGE_CAPTURE) {

            // initialize imageBitmap to null
            imageBitmap = null;

            // case 1: image selected from gallery (data_back is not null and has data)
            if (data_back != null && data_back.getData() != null) {
                // load the image from the returned uri (gallery selection)
                Uri selectedImageUri = data_back.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    // clear currentPath since we are not using the temp file
                    currentPath = null;
                } catch (IOException e) {
                    Log.e(TAG, "error loading image from gallery: " + e.getMessage());
                    Toast.makeText(this, "failed to load image from gallery.", Toast.LENGTH_SHORT).show();
                }
            }
            // case 2: image taken with camera (data_back is null, the result is in currentPath)
            else if (currentPath != null) {
                // load the image from the temporary file created by the camera intent
                imageBitmap = BitmapFactory.decodeFile(currentPath);
            }

            // check if an image was successfully loaded/captured
            if (imageBitmap != null) {
                iVPhoto.setImageBitmap(imageBitmap);
                ProgressDialog pD = new ProgressDialog(this);
                pD.setTitle("Sent Prompt");
                pD.setMessage("waiting for response...");
                pD.setCancelable(false);
                pD.show();
                String prompt = PHOTO_PROMPT;
                geminiManager.sendTextWithPhotoPrompt(prompt, imageBitmap,
                        new GeminiCallback() {
                            @Override
                            public void onSuccess(String result) {
                                pD.dismiss();
                                tVDescription.setText(result);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                pD.dismiss();
                                tVDescription.setText("Error: " + error.getMessage());
                                Log.e(TAG, "onActivityResult/ Error: " + error.getMessage());
                            }
                        });
            }
        }
    }

    public void onPickPic(View view) {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            String filename = "tempfile";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imgFile = File.createTempFile(filename,".jpg",storageDir);
            currentPath = imgFile.getAbsolutePath();

            // get the secure uri for the camera output
            Uri imageUri = FileProvider.getUriForFile(
                    GeminiActivity.this,
                    "com.example.lockin_prototype.fileprovider",
                    imgFile
            );

            takePicIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

            // 2. create the gallery intent (base intent for the chooser)
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType("image/*");

            // 3. create the chooser intent with the gallery intent as the base
            Intent chooserIntent = Intent.createChooser(pickIntent, "pick a picture");

            // add the camera intent as an option in the chooser
            if (takePicIntent.resolveActivity(getPackageManager()) != null) {
                chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[] { takePicIntent }
                );
            }

            // 4. start the chooser
            startActivityForResult(chooserIntent, REQUEST_CHOOSER);
        }
        catch (IOException e) {
            Toast.makeText(GeminiActivity.this,"Failed to create temporary file",Toast.LENGTH_LONG);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st = item.getTitle().toString();

        if (st.equals("Sign")) {
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        else if (st.equals("Firebase")) {
            Intent si = new Intent(this, FirebaseActivity.class);
            startActivity(si);
        }
        else if (st.equals("Gallery")) {
            Intent si = new Intent(this, GalleryActivity.class);
            startActivity(si);
        }
        else if (st.equals("Take a pic")) {
            Intent si = new Intent(this, CameraActivity.class);
            startActivity(si);
        }
        else if (st.equals("Notification")) {
            Intent si = new Intent(this, NotificationActivity.class);
            startActivity(si);
        }
        else if (st.equals("Charger info")) {
            Intent si = new Intent(this, ChargerInfoActivity.class);
            startActivity(si);
        }
        else if (st.equals("Set timer")) {
            Intent si = new Intent(this, TimerActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }
}

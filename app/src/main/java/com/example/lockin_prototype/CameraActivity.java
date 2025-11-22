package com.example.lockin_prototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 200;

    public ImageView iVCamPic;

    public StorageReference refStorage;

    public String lastImageName;
    public String currentPath;
    public File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        iVCamPic = findViewById(R.id.iVCamPic);
        refStorage = FirebaseStorage.getInstance().getReference().child("images");
    }


    public void onTakePic(View view) {
        takePic();
    }

    private void takePic() {
        String filename = "pic_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imgFile = File.createTempFile(filename, ".jpg", storageDir);
            currentPath = imgFile.getAbsolutePath();

            Uri imageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    imgFile
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CAMERA);
            }

        } catch (IOException e) {
            Toast.makeText(this, "File creation failed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data_back) {
        super.onActivityResult(requestCode, resultCode, data_back);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CAMERA) {

            ProgressDialog pd = ProgressDialog.show(this, "Uploading", "Please wait...", true);

            Date date = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            lastImageName = df.format(date);

            StorageReference fileRef = refStorage.child(lastImageName + ".jpg");

            Bitmap bitmap = BitmapFactory.decodeFile(currentPath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] data = baos.toByteArray();

            fileRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        pd.dismiss();
                        Toast.makeText(this, "Upload complete!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        pd.dismiss();
                        Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    public void onSave(View view){

        if (lastImageName == null) {
            Toast.makeText(this, "No image uploaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileRef = refStorage.child(lastImageName + ".jpg");

        try {
            localFile = File.createTempFile("downloaded_", ".jpg");
        } catch (IOException e) {
            Toast.makeText(this, "Local file error", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = ProgressDialog.show(this, "Downloading", "Please wait...", true);

        fileRef.getFile(localFile)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();

                    Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iVCamPic.setImageBitmap(bmp);

                    Toast.makeText(this, "Download OK!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(this, "Download failed!", Toast.LENGTH_SHORT).show();
                });
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
        else if (st.equals("Ai")) {
            Intent si = new Intent(this, GeminiActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }

}
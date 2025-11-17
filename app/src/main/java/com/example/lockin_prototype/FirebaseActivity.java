package com.example.lockin_prototype;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;

public class FirebaseActivity extends AppCompatActivity {

    public EditText eTKey, eTcontent;
    public ListView lVFirebaseData;

    // firebase database reference
    public DatabaseReference refDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

    }

    public void onEnter(View view) {
    }
}
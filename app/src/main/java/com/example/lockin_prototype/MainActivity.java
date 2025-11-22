package com.example.lockin_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    public EditText eTEmail, eTPassword;
    public TextView tVFBAuth;

    public FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eTEmail = findViewById(R.id.eTEmail);
        eTPassword = findViewById(R.id.eTPassword);
        tVFBAuth = findViewById(R.id.tVFBAuth);
    }

    public void registerUser() {
        // extract input text and trim whitespace
        String email = eTEmail.getText().toString().trim();
        String password = eTPassword.getText().toString().trim();

        // check the fields are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields.", Toast.LENGTH_LONG).show();
            return;
        }
        // use firebase auth to create a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser(); // user creation success: get the user object
                            String userId = user != null ? user.getUid() : "N/A"; // get the user id

                            // update the output field
                            tVFBAuth.setText("User ID: " + userId);
                        }
                        else { // user creation failed

                            // helpful error message
                            String errorMessage;
                            if (task.getException() != null) errorMessage = task.getException().getMessage();
                            else errorMessage = "Error";

                            // update the output field - error
                            tVFBAuth.setText("Registration Failed: " + errorMessage);
                        }
                    }
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
            // none
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
        else if (st.equals("Ai")) {
            Intent si = new Intent(this, GeminiActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEnter(View view) {
        registerUser();
    }
}
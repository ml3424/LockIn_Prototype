package com.example.lockin_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TimerActivity extends AppCompatActivity {

    public TextView TvCountdown;
    public Button BtnStartTimer;
    public CountDownTimer countDownTimer;
    public long timeLeftInMillis = 10000; // 10 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        TvCountdown = findViewById(R.id.TvCountdown);
        BtnStartTimer = findViewById(R.id.BtnStartTimer);

        updateCountDownText();
    }

    public void onclick_start(View view) {
        startTimer();
    }

    public void updateCountDownText() {
        // Calculate minutes and seconds from milliseconds
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        // Format the time as minutes:seconds
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);

        // Display the formatted time
        TvCountdown.setText(timeFormatted);
    }

    private void startTimer() {
        timeLeftInMillis = 10000; // init the countdown time to 10 seconds

        // create a new CountDownTimer object
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {

            // called every 1000 milliseconds (1 sec)
            @Override
            public void onTick(long millisUntilFinished) {
                // update the remaining time
                timeLeftInMillis = millisUntilFinished;
                // update the TextView to show the remaining time
                updateCountDownText();
            }

            // called when the timer finishes
            @Override
            public void onFinish() {
                TvCountdown.setText("00:00");
                Toast.makeText(TimerActivity.this, "TIME UP!", Toast.LENGTH_LONG).show();
                BtnStartTimer.setEnabled(true);
            }
        }.start(); // start the timer

        // disable the button while the timer is running
        BtnStartTimer.setEnabled(false);
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
        else if (st.equals("Ai")) {
            Intent si = new Intent(this, GeminiActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }
}
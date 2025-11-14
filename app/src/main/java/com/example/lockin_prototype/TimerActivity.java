package com.example.lockin_prototype;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
}
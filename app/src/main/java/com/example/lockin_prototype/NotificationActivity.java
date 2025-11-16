package com.example.lockin_prototype;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {

    // constants for the notification channel and keys
    private static final String CHANNEL_ID = "reminder_channel_id";
    private static final String CHANNEL_NAME = "Daily Reminders";
    private static final int NOTIFICATION_ID = 1;

    public EditText eTMessage;
    public TimePicker timePicker;
    public Button btnSetNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        eTMessage = findViewById(R.id.eTMessage); // the text input field
        timePicker = findViewById(R.id.timePicker1);           // the embedded time picker
        btnSetNoti = findViewById(R.id.btnSetNoti);

        NotificationReceiver.createNotificationChannel(this);
    }

    // function to set the alarm manager
    private void scheduleNotification() {
        // get the message from edittext
        String notificationMessage = eTMessage.getText().toString();
        if (notificationMessage.trim().isEmpty()) {
            Toast.makeText(this, "please enter a message first", Toast.LENGTH_SHORT).show();
            return;
        }

        // get the selected time from the timepicker
        int hour, minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        }
        else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        // set the calendar object for the exact time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // if the time is in the past, schedule for the next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // create the intent and attach the user's custom message
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.MESSAGE_KEY, notificationMessage);

        // use current time as request code for uniqueness
        int requestCode = (int) System.currentTimeMillis() % 10000;

        // create the pendingintent (the key for the alarmmanager)
        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // set the alarm using alarmmanager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!am.canScheduleExactAlarms()) {
                    // permission is not granted, request it from the user
                    Intent exactAlarmIntent = new Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse("package:" + getPackageName())
                    );
                    startActivity(exactAlarmIntent);
                    Toast.makeText(this, "please grant 'schedule exact alarms' permission", Toast.LENGTH_LONG).show();
                    return; // stop until permission is granted
                }
            }
            am.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pi
            );

            Toast.makeText(this, "reminder set for: " + hour + ":" + minute, Toast.LENGTH_LONG).show();
        }
    }


    public void onclick_set_noti(View view) {
        scheduleNotification();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String st = item.getTitle().toString();

        if (st.equals("Sign")) {
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        else if (st.equals("Firebase")) {
            // Add your 'Somthing' logic here
        }
        else if (st.equals("Gallery")) {
            // Add your 'Gallery' logic here
        }
        else if (st.equals("Take a pic")) {
            // Add your 'Take a pic' logic here
        }
        else if (st.equals("Notification")) {
            // none
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
            // Add your 'Ai' logic here
        }

        return super.onOptionsItemSelected(item);
    }
}
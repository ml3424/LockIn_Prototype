package com.example.lockin_prototype;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChargerInfoActivity extends AppCompatActivity {

    public static ChargerReceiver batteryReceiver = new ChargerReceiver();
    public static boolean isRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_info);

    }

    // function to register the broadcast receiver (Button 1 functionality)
    private void registerBroadcastListener() {
        if (!isRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

            getApplicationContext().registerReceiver(batteryReceiver, filter); // using the Application Context which allows to listen even when the Activity is paused or destroyed.
            isRegistered = true;
            Toast.makeText(this, "Listener activated.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Listener already active.", Toast.LENGTH_SHORT).show();
        }
    }

    // function to unregister the broadcast receiver (Button 2 functionality)
    private void unregisterBroadcastListener() {
        if (isRegistered)
        {
            // unregister the receiver using the same Application Context
            getApplicationContext().unregisterReceiver(batteryReceiver);
            isRegistered = false;
            Toast.makeText(this, "Listener deactivated.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Listener is not active.", Toast.LENGTH_SHORT).show();
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
            // Add your 'Somthing' logic here
        }
        else if (st.equals("Gallery")) {
            // Add your 'Gallery' logic here
        }
        else if (st.equals("Take a pic")) {
            // Add your 'Take a pic' logic here
        }
        else if (st.equals("Notification")) {
            Intent si = new Intent(this, NotificationActivity.class);
            startActivity(si);
        }
        else if (st.equals("Charger info")) {
            // none
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

    public void onStartListening(View view) {
        registerBroadcastListener();
    }

    public void onStopListening(View view) {
        unregisterBroadcastListener();
    }
}
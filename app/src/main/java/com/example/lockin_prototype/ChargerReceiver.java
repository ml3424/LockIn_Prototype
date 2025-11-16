package com.example.lockin_prototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ChargerReceiver extends BroadcastReceiver {

    // this method is automatically called when a registered broadcast is received
    @Override
    public void onReceive(Context context, Intent intent) {
        // check if the intent action matches the battery plugged/unplugged action
        if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
            // show a toast when the charger is connected
            Toast.makeText(context, "CHARGER CONNECTED!", Toast.LENGTH_SHORT).show();
        }
        else if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
            // show a toast when the charger is disconnected
            Toast.makeText(context, "CHARGER DISCONNECTED!", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.backgroundapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.Toast

class BatteryStatusReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING

        if (isCharging) {
            Toast.makeText(context, "Perangkat sedang mengisi daya", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Perangkat tidak mengisi daya", Toast.LENGTH_SHORT).show()
        }
    }
}

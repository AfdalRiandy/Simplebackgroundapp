package com.example.backgroundapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class BackgroundDownloadService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BackgroundDownloadService", "Layanan dimulai. Mengunduh data...")

        Thread {
            try {
                Thread.sleep(3000) // Simulasi pengunduhan
                Log.d("BackgroundDownloadService", "Pengunduhan selesai")
                stopSelf() // Hentikan layanan setelah selesai
            } catch (e: InterruptedException) {
                Log.e("BackgroundDownloadService", "Pengunduhan gagal", e)
            }
        }.start()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Tidak digunakan karena ini bukan layanan bound
        return null
    }
}

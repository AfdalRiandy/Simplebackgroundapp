package com.example.backgroundapp

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnDownload: Button = findViewById(R.id.btnDownload)
        val btnClearData: Button = findViewById(R.id.btnClearData)
        val btnCheckBatteryStatus: Button = findViewById(R.id.btnCheckBatteryStatus)
        val tvDownloadedData: TextView = findViewById(R.id.tvDownloadedData)

        btnDownload.setOnClickListener {
            // Buat dialog input
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Masukkan Link Unduhan")

            // Tambahkan EditText ke dialog
            val input = EditText(this)
            input.hint = "https://contoh.com/file"
            builder.setView(input)

            // Tambahkan tombol "Unduh" ke dialog
            builder.setPositiveButton("Unduh") { dialog, _ ->
                val downloadUrl = input.text.toString()
                if (downloadUrl.isNotEmpty()) {
                    // Mulai WorkManager dengan URL yang dimasukkan
                    val downloadRequest = OneTimeWorkRequestBuilder<BackgroundDownloadWorker>()
                        .setInputData(Data.Builder().putString("downloadUrl", downloadUrl).build())
                        .build()

                    WorkManager.getInstance(this).enqueue(downloadRequest)

                    WorkManager.getInstance(this).getWorkInfoByIdLiveData(downloadRequest.id)
                        .observe(this, Observer { workInfo ->
                            if (workInfo != null && workInfo.state.isFinished) {
                                val outputData = workInfo.outputData.getString("downloadedData") ?: "Data tidak ditemukan"
                                tvDownloadedData.text = outputData
                            }
                        })
                } else {
                    Toast.makeText(this, "Link tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            // Tambahkan tombol "Batal" ke dialog
            builder.setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

            // Tampilkan dialog
            builder.create().show()
        }

        btnClearData.setOnClickListener {
            tvDownloadedData.text = "Data yang diunduh akan muncul di sini"
        }

        btnCheckBatteryStatus.setOnClickListener {
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                this.registerReceiver(null, ifilter)
            }

            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = level / scale.toFloat() * 100

            val isCharging = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING

            val statusMessage = if (isCharging) {
                "Baterai sedang mengisi. Sisa baterai: ${"%.2f".format(batteryPct)}%"
            } else {
                "Baterai tidak mengisi. Sisa baterai: ${"%.2f".format(batteryPct)}%"
            }

            Toast.makeText(this, statusMessage, Toast.LENGTH_LONG).show()
        }
    }
}

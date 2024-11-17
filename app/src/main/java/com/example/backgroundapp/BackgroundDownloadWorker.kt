package com.example.backgroundapp

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.work.Data

class BackgroundDownloadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val downloadUrl = inputData.getString("downloadUrl") ?: return Result.failure()
        var result = ""

        try {
            val url = URL(downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = BufferedInputStream(connection.inputStream)
                val reader = BufferedReader(InputStreamReader(inputStream))
                result = reader.use(BufferedReader::readText)
                Log.d("BackgroundDownloadWorker", "Data yang diunduh: $result")
            } else {
                Log.e("BackgroundDownloadWorker", "Error dalam mengunduh data. Kode respon: $responseCode")
                return Result.failure()
            }
            connection.disconnect()
        } catch (e: Exception) {
            Log.e("BackgroundDownloadWorker", "Gagal mengunduh data", e)
            return Result.failure()
        }

        val outputData = Data.Builder()
            .putString("downloadedData", result)
            .build()

        Log.d("BackgroundDownloadWorker", "Pengunduhan selesai")
        return Result.success(outputData)
    }
}

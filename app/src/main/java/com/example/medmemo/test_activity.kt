package com.example.medmemo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class test_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val testButton = findViewById<Button>(R.id.testButton)
        testButton.setOnClickListener {
            sendTestRequest()
        }
    }
    private fun sendTestRequest() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://10.0.2.2/FinMed_nemu-master/test.php") // 実際のAPI URL
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("API_TEST", "HTTP Code: ${response.code}")
                Log.d("API_TEST", "Response Body: $body")
                runOnUiThread {
                    Toast.makeText(
                        this@test_activity,
                        "レスポンス: $body",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_TEST", "Request failed: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        this@test_activity,
                        "リクエストが失敗しました: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}
package com.example.medmemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MedEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_med_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 画面デザインで定義したオブジェクトを変数として宣言する。
        val titleText = findViewById<TextView>(R.id.textView)   // ページのタイトルの部
        titleText.text = "薬の編集画面"


        val MedName = findViewById<TextView>(R.id.MedName)
        val remainingEditText = findViewById<EditText>(R.id.remainingEditText)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val medImgEditButton = findViewById<Button>(R.id.medImgEditButton)
        val medImg = findViewById<ImageButton>(R.id.medImg)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val registrationButton = findViewById<Button>(R.id.registrationButton)

        //前の画面からMedNoを取ってくる
        val medNo = intent.getIntExtra("medNo", -1)
        val medName = intent.getStringExtra("medName")
        val remainingCount = intent.getStringExtra("remainingCount")
        val limitDate = intent.getStringExtra("limitDate")
        Log.d("MedNoだよ", "うけとりました medNo= $medNo")

        MedName.text = medName ?: ""
        remainingEditText.setText(remainingCount ?: "")
        dateEditText.setText(limitDate ?: "")

        // 薬名を表示
        MedName.text = medName ?: ""

        // キャンセルボタンを押されたら前の画面に戻る
        cancelButton.setOnClickListener {
            finish()
        }



        // registrationButtonを押されたときの処理
        registrationButton.setOnClickListener {
            val newRemainingCount = remainingEditText.text.toString()
            val newLimitDate = dateEditText.text.toString()



            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようなデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ（APIに渡したいパラメータを設定）
            val requestBodyJson = JSONObject().apply {
                put("userId", MyApplication.getInstance().loginUserId)
                put("medNo", medNo)
                put("remainingCnt", newRemainingCount.toInt())
                put("expDate", newLimitDate)
            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

            // Requestを作成
            val request = Request.Builder()
                .url(MyApplication.getInstance().apiUrl + "userMedUpdate.php")
                .post(requestBody)
                .build()
            // リクエスト送信（非同期処理）
            client.newCall(request).enqueue(object : Callback {
                // １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    // ログ
                    println("なかみだよ～" + body)

                    // APIから取得したJSON文字列をJSONオブジェクトに変換
                    runOnUiThread {
                        val json = JSONObject(body)
                        val status = json.optString("status", json.optString("result", "error"))

                        if (status == "success") {
                            Toast.makeText(
                                applicationContext,
                                "更新しました",
                                Toast.LENGTH_SHORT
                            ).show()

                            // ★ MedicineActivity へ戻る Intent を新規作成
                            val backIntent = Intent(this@MedEditActivity, MedicineActivity::class.java)
                            backIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK

                            startActivity(backIntent)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    // ２－３－２－１．エラーメッセージをトースト表示する
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "リクエストに失敗しました。",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            })


        }
    }
}
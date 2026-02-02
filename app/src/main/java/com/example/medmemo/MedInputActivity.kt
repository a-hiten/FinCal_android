package com.example.medmemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

class MedInputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_med_input)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //　画面タイトルを表示する
        val titleText = findViewById<TextView>(R.id.textView)   // ページのタイトルの部
        titleText.text = "薬の追加"

        //画面デザインで定義したオブジェクトを変数として宣言する
        val medName = findViewById<TextView>(R.id.medName)
        val remainingEditText = findViewById<EditText>(R.id.remainingEditText)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)
        val medImgButton = findViewById<Button>(R.id.medImgButton)
        val medImg = findViewById<ImageView>(R.id.medImg)
        val registrationButton = findViewById<Button>(R.id.registrationButton)


        val medNo = intent.getIntExtra("medNo", -1)
        val medNameValue = intent.getStringExtra("medName")

        medName.text = medNameValue ?: ""

        // 登録するボタンを押したとき
        registrationButton.setOnClickListener {
            val remainingStr = remainingEditText.text.toString()
            val dateStr = dateEditText.text.toString()

            // 入力チェック
            if (remainingStr.isBlank() || dateStr.isBlank()) {
                Toast.makeText(this, "すべて入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val remainingCnt = remainingStr.toIntOrNull()
            if (remainingCnt == null) {
                Toast.makeText(this, "残り回数は数字で入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // グローバル変数のログインユーザーIDを取得。
            val loginUserId = MyApplication.getInstance().loginUserId

            // ログイン認証APIをリクエストして入力ユーザのログイン認証を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようなデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ（APIに渡したいパラメータを設定）
            val requestBodyJson = JSONObject().apply {
                put("userId", loginUserId)
                put("medNo", medNo)
                put("expDate", dateStr)
                put("remainingCnt", remainingCnt)
            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

            // Requestを作成
            val request = Request.Builder()
                .url(MyApplication.getInstance().apiUrl + "medAdd.php")
                .post(requestBody)
                .build()

            // リクエスト送信（非同期処理）
            client.newCall(request).enqueue(object : Callback {
                // １－２－２－１．正常にレスポンスを受け取った時(コールバック処理)
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    // ログ
                    Log.d("aaa","なかみだよ～" + body)

                    // APIから取得したJSON文字列をJSONオブジェクトに変換
                    runOnUiThread {
                        val json = JSONObject(body)
                        val status = json.optString("status", json.optString("result", "error"))

                        if (status != "success") {
                            val errorMsg = json.optString("error", "エラーが発生しました。")
                            runOnUiThread {
                                Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT)
                                    .show()
                                return@runOnUiThread
                            }
                        }
                        // ホーム画面へ戻る
                        val intent = Intent(this@MedInputActivity, MedicineActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
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
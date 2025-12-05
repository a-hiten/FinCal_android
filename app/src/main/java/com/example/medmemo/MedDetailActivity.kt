package com.example.medmemo

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MedDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_med_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 画面デザインで定義したオブジェクトを変数として宣言する。
        val titleText = findViewById<TextView>(R.id.textView)   // ページのタイトルの部
        titleText.text = "薬の編集画面"

//        val userIdEdit = findViewById<EditText>(R.id.userIdEdit)        // メールアドレス入力

        val medNameCon = findViewById<TextView>(R.id.medNameConText)
        val ageCon = findViewById<TextView>(R.id.ageCntText)
        val medCon = findViewById<TextView>(R.id.medConText)
        val medType = findViewById<TextView>(R.id.medTypeConText)
        val medTakeTime = findViewById<TextView>(R.id.takeTimeConText)
        val effect = findViewById<TextView>(R.id.effectConText)
        val contraindication =findViewById<TextView>(R.id.comboConText)
        val returnButton = findViewById<ImageButton>(R.id.returnButton)
        val editButton = findViewById<Button>(R.id.editButton)

        val medNo = intent.getIntExtra("medNo", -1)
        Log.d("MedNoだよ","うけとりました medNo= $medNo")



        medNameCon.setOnClickListener {

            //グローバル変数のログインユーザIDを取得
            val loginUserId = MyApplication.getInstance().loginUserId

            // ログイン認証APIをリクエストして入力ユーザのログイン認証を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようなデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ（APIに渡したいパラメータを設定）
            val requestBodyJson = JSONObject().apply {
                put("userId", loginUserId)
            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

            // Requestを作成
            val request = Request.Builder()
                .url(MyApplication.getInstance().apiUrl + "medDetail.php")
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

                        if (status != "success") {
                            val errorMsg = json.optString("error", "エラーが発生しました。")
                            runOnUiThread {
                                Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT)
                                    .show()
                                return@runOnUiThread
                            }
                        }
                        val medicals = json.optJSONObject("medicineInfo") ?: JSONObject()

                        // ログ
                        Log.d("UserMed", "loginUserId = $loginUserId")
                        Log.d("UserMed", "whispers length = ${medicals.length()}")

                        medNameCon.text = medicals.optString("medName")
                        ageCon.text = medicals.optInt("ageLimit").toString()
                        medCon.text = medicals.optString("dosage")
                        medType.text = medicals.optString("medType")
                        medTakeTime.text = medicals.optInt("medTakeTime").toString()
                        effect.text = medicals.optString("effect")
                        contraindication.text = medicals.optString("contraindication")

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
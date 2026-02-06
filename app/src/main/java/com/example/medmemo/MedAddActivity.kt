package com.example.medmemo

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medmemo.OverflowMenu.Companion.handleMenuItemSelected
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

class MedAddActivity : OverflowMenu() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_med_add)

        //追記
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //．画面デザインで定義したオブジェクトを変数として宣言する。
        val titleText = findViewById<TextView>(R.id.textView)   // ページのタイトルの部
        titleText.text = "薬の追加"

        val searchEdit = findViewById<EditText>(R.id.searchEdit)
        val searchButton = findViewById<Button>(R.id.searchButton)

        val recyclerView = findViewById<RecyclerView>(R.id.userMedlineRecycle)      // リストの内容は薬の名前行情報を指定している
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchButton.setOnClickListener {
            val searchWord = searchEdit.text.toString()

            if(searchWord.isBlank()){
                Toast.makeText(this,"検索欄に文字を入力してください。",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ログイン認証APIをリクエストして入力ユーザのログイン認証を行う
            // HTTP接続用インスタンス生成
            val client = OkHttpClient()
            // JSON形式でパラメータを送るようなデータ形式を設定
            val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
            // Bodyのデータ（APIに渡したいパラメータを設定）
            val requestBodyJson = JSONObject().apply {
                put("string", searchWord)
            }
            // BodyのデータをAPIに送る為にRequestBody形式に加工
            val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

            // Requestを作成
            val request = Request.Builder()
                .url(MyApplication.getInstance().apiUrl + "deleteCtl.php")
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

                        val medNameList = mutableListOf<MedNameRowData>()
                        val mednames = json.optJSONArray("medList") ?: JSONArray()

                        // ログ
                        Log.d("medName", "medNameList = $medNameList")
                        Log.d("medName", "mednames = ${mednames.length()}")

                        for (i in 0 until mednames.length()) {
                            val obj = mednames.getJSONObject(i)
                            val data = MedNameRowData(
                                medNo = obj.optInt("medNo"),
                                medName = obj.optString("medName"),
                            )
                            medNameList.add(data)
                        }
                        val recyclerView = findViewById<RecyclerView>(R.id.userMedlineRecycle)
                        recyclerView.adapter = MedNameAdapter(medNameList, this@MedAddActivity)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return handleMenuItemSelected(this, item)
    }
}
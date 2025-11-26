package com.example.medmemo

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MedicineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medicine)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 画面デザインで定義したオブジェクトを変数として指定する。
        val titleText = findViewById<TextView>(R.id.textView)   // ページのタイトルの部
        titleText.text = "現在使用中のお薬"

        val recyclerView = findViewById<RecyclerView>(R.id.userMedlineRecycle)      // リストの内容は自分のお薬行情報を参照している
        recyclerView.layoutManager = LinearLayoutManager(this)





    }
}
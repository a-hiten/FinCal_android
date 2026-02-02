package com.example.medmemo

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        val remainingEditText = findViewById<TextView>(R.id.remainingEditText)
        val dateEditText = findViewById<TextView>(R.id.dateEditText)
        val medImgButton = findViewById<Button>(R.id.medImgButton)
        val medImg = findViewById<ImageView>(R.id.medImg)
        val registrationButton = findViewById<Button>(R.id.registrationButton)


//        val medNo = intent.getIntExtra("medNo", -1)
        val medNameValue = intent.getStringExtra("medName")

        medName.text = medNameValue ?: ""

    }
}
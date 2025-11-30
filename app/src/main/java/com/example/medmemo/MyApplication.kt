package com.example.medmemo

import android.app.Application

//１．Applicationクラスを継承する
class MyApplication : Application(){

    //２．グローバル変数loginUserIdを文字列型で宣言して、空文字で初期化する
    var loginUserId: String = ""    //変更される想定だからvarにしている

    //３．グローバル変数apiUrlを文字列型で宣言して、各チームのAPIのホームディレクトリで初期化する
    //ホームディレクトリ例 ： http://click.ecc.ac.jp/ecc/whisperSystem/
    val apiUrl: String = "http://10.0.2.2/FinMed_nemu-master/"

    //４．AndroidManifest設定
    companion object {
        // ４－１．このApplicationクラスのインスタンス（=自分自身）を保存するための変数を用意する
        // ※あとでgetInstance()からアクセスするために使う
        private lateinit var instance: MyApplication    // 宣言（後で初期化される）
        // ４－２．他の画面（Activityなど）からこのApplicationクラスにアクセスできるようにするための関数を追加する。
        fun getInstance(): MyApplication {
            return instance
        }
    }
    // ５．アプリ起動時に一度だけ呼ばれる初期化処理を定義するため、onCreate() をオーバーライドする
    override fun onCreate() {       // 初期化される
        super.onCreate()
        // ５－１．自分自身（Applicationクラス）をinstanceに入れる
        instance = this
    }

}
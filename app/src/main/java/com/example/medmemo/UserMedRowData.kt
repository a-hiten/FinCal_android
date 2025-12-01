package com.example.medmemo

import android.provider.ContactsContract.Data

data class UserMedRowData(
    // APIのinputパラメータと名前を統一する
    val userId : String,   //ユーザID
    val userName : String, //ユーザ名
    val userMedNo : Int,   //ユーザの薬番
    val medNo : Int,       //薬の番号
    val medName : String,  //薬の名前
    val expDate : String,    //薬の使用期限
    val effect : String,    //効能・効果
    val remaining : String, //薬の残り回数
    val medImage : String,  //薬の画像パス
)

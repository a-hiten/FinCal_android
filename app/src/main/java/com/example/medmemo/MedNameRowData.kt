package com.example.medmemo

data class MedNameRowData(
    // APIのinputパラメータと名前を統一する
 
    val medName : String,   //薬の名前
    val data : Int,         //薬の使用期限
    val effect : String,    //効能・効果
    val remaining : String, //薬の残り回数
)

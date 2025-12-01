package com.example.medmemo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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


class UserMedAdapter(private val dataset:MutableList<UserMedRowData>,private val context: Context) : RecyclerView.Adapter<UserMedAdapter.ViewHolder>() {

    //ビューホルダー（内部クラス）
    class ViewHolder(item: View) :RecyclerView.ViewHolder(item) {

        //画面デザインで定義したオブジェクトを変数として宣言する
        val medName : TextView
        val effectCon : TextView
        val remainingCon : TextView
        val dateCon : TextView
        val usingButton : Button
        val deletingButton :ImageButton
        val medImg :ImageView

        init {
            medName = item.findViewById(R.id.MedNameText)
            effectCon = item.findViewById(R.id.effectConText)
            remainingCon = item.findViewById(R.id.remainingConText)
            dateCon = item.findViewById(R.id.dateConText)
            usingButton = item.findViewById(R.id.usingButton)
            deletingButton = item.findViewById(R.id.deletingButton)
            medImg = item.findViewById(R.id.medImg)
        }
    }

    //ビューホルダーバインド時（onBindViewHolder処理）
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 自分のお薬行情報の画面デザイン（usermed_recycle_row）をViewHolderに設定し、戻り値にセットする。
        val  view = LayoutInflater.from(parent.context).inflate(R.layout.usermed_recycle_row, parent,false)
        return ViewHolder(view)
    }

    //ビューホルダーバインド時（onBindViewHolder処理）
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // datasetから現在の行のデータを取得する
        val item = dataset[position]

        // 取得したデータを画面に表示している項目を設定する。
        // ビューホルダーのオブジェクトに対象行のデータ（薬名、効果、残数、使用期限）をセットする
        holder.medName.text = dataset[position].medName
        holder.effectCon.text = dataset[position].effect
        holder.remainingCon.text = dataset[position].remaining
        holder.dateCon.text = dataset[position].expDate.toString()


        Log.d("aaa",holder.medName.text.toString())

        // HTTP接続用インスタンス生成
        val client = OkHttpClient()
        // JSON形式でパラメータを送るようデータ形式を設定
        val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()
        // Bodyのデータ(APIに渡したいパラメータを設定)
        val requestBodyJson = JSONObject().apply {
            put("userId", MyApplication.getInstance().loginUserId)
            put("userMedNo",item.userMedNo)
            put("medNo", item.medName)
        }
        Log.d("ぱらめーたのなまえかくにん", requestBodyJson.toString())

        // BodyのデータをAPIに送るためにRequestBody形式に加工
        val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
        // Requestを作成(先ほど設定したデータ形式とパラメータ情報をもとにリクエストデータを作成)
        val request = Request.Builder()
            .url(MyApplication.getInstance().apiUrl + "まだだよ.php")
            .post(requestBody) // リクエストするパラメータ設定
            .build()

        client.newCall(request).enqueue(object : Callback {
            // 正常にレスポンスを受け取った時(コールバック処理)
            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string().orEmpty()
                (context as? android.app.Activity)?.runOnUiThread {
                    //一次的に追記（落ちないようにしている）
                    if (!bodyStr.trim().startsWith("{")) {
                        Toast.makeText(context, "サーバーエラー（JSON形式ではありません）", Toast.LENGTH_SHORT).show()
                        Log.d("server_error", bodyStr) // ←エラーメッセージ確認できる
                        return@runOnUiThread
                    }


                    val json = JSONObject(bodyStr)
                    val status = json.optString("status", json.optString("result", "error"))


                    // JSONデータがエラーの場合、受け取ったエラーメッセージをトースト表示して処理を終了させる
                    if (status != "success") {
                        val errMsg = json.optString("error", "失敗しました。")
                        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT)
                            .show()
                        return@runOnUiThread
                    }
                }
            }

            // リクエストが失敗した時(コールバック処理)
            override fun onFailure(call: Call, e: IOException) {
                // エラーメッセージをトースト表示する
                (context as? android.app.Activity)?.runOnUiThread {
                    Toast.makeText(context, "リクエストが失敗しました", Toast.LENGTH_SHORT)
                        .show()
                    return@runOnUiThread
                }
            }
        })
    }


    override fun getItemCount(): Int {
        // 行リストの件数（データセットのサイズ）を戻り値にセットする
        return dataset.size
    }
}
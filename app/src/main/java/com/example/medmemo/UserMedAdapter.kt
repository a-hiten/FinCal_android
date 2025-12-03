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
        holder.remainingCon.text = "${dataset[position].remaining} 回"
        holder.dateCon.text = dataset[position].expDate.toString()
        //確認用ログ
        Log.d("aaa",holder.medName.text.toString())
        Log.d("aaa",holder.effectCon.text.toString())
        Log.d("aaa",holder.remainingCon.text.toString())
        Log.d("aaa",holder.dateCon.text.toString())

        holder.medName.setOnClickListener {
            val intent = android.content.Intent(context, MedDetailActivity::class.java).apply {
                putExtra("medNo", item.medNo)
                putExtra("medName", item.medName)
                putExtra("effect", item.effect)
                putExtra("remaining", item.remaining)
                putExtra("expDate", item.expDate)
                putExtra("medImage", item.medImage)
            }
            context.startActivity(intent)
        }

        //画像表示処理
        // 画像パスが登録されているかチェック
        if (item.medImage.isNotEmpty()) {
            // ファイルからBitmapを読み込む
            val imgFile = java.io.File(item.medImage)
            if (imgFile.exists()) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.medImg.setImageBitmap(bitmap)
            } else {
                // ファイルが存在しない場合はデフォルト画像を表示
                holder.medImg.setImageResource(R.drawable.noimage)
            }
        } else {
            // パスが空ならデフォルト画像を表示
            holder.medImg.setImageResource(R.drawable.noimage)
        }

        //usingButtonのクリックリスナーを生成する
        holder.usingButton.setOnClickListener {
            // String型のremainingをIntに変換 変換できなかったら０にする
            val currentRemaining = item.remaining.toIntOrNull() ?: 0

            //残数が０より大きい場合だけ減らす
            if (currentRemaining > 0) {
                //残数を一減らす
                val newRemaining = currentRemaining - 1

                // datasetのremainingを更新する
                item.remaining = newRemaining.toString()

                // TextViewに反映して後ろに「回」をつける
                holder.remainingCon.text = "${newRemaining} 回"

                // 残数が０になったら赤色、それ以外は黒にする
                if (newRemaining == 0) {
                    holder.remainingCon.setTextColor(android.graphics.Color.RED)
                } else {
                    holder.remainingCon.setTextColor(android.graphics.Color.BLACK)
                }
            } else {
                //減らせなくなったらトーストをだす。
                Toast.makeText(context, "これ以上減らせません", Toast.LENGTH_SHORT).show()
            }
        }

        //deletingButtonのクリックリスナーを生成する
        holder.deletingButton.setOnClickListener{
            //dataset から押されたアイテムを削除
            dataset.removeAt(position)

            //RecyclerView に「この行が削除された」と通知
            notifyItemRemoved(position)

            //もしデータの順番に影響がある場合は通知を追加
            notifyItemRangeChanged(position, dataset.size)

            //必要に応じてトースト表示
            Toast.makeText(context, "削除しました", Toast.LENGTH_SHORT).show()
        }

        /*
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

        */
    }


    override fun getItemCount(): Int {
        // 行リストの件数（データセットのサイズ）を戻り値にセットする
        return dataset.size
    }
}
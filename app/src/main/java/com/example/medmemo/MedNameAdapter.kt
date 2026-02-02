package com.example.medmemo

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView


class MedNameAdapter(private val dataset:MutableList<MedNameRowData>,private val context: Context) : RecyclerView.Adapter<MedNameAdapter.ViewHolder>() {

    //ビューホルダー（内部クラス）
    class ViewHolder(item: View) :RecyclerView.ViewHolder(item) {

        //画面デザインで定義したオブジェクトを変数として宣言する
        val MedButton : Button


        init {
            MedButton = item.findViewById(R.id.MedButton)
        }
    }

    //ビューホルダーバインド時（onBindViewHolder処理）
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //薬の名前行情報の画面デザイン（medname_recycle_row）をViewHolderに設定し、戻り値にセットする。
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medname_recycle_row,parent,false)
        return  ViewHolder(view)
    }

    //ビューホルダーバインド時（onBindViewHolder処理）
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //datasetから現在の行のデータを取得する
        val item = dataset[position]

        //取得したデータを画面に表示している項目を設定する。
        //ビューホルダーのオブジェクトに対象行のデータ（薬名）をセットする
        holder.MedButton.text = dataset[position].medName
        //ログ
        Log.d("aaa",holder.MedButton.text.toString())

        // ボタンに薬名を表示
        holder.MedButton.text = item.medName

        // ログ（確認用）
        Log.d("MedNameAdapter", item.medName)

        // MedNameButtonボタンを押したときだけ遷移
        holder.MedButton.setOnClickListener {
            val intent = Intent(context, MedInputActivity::class.java).apply {
                putExtra("medNo", item.medNo)
                putExtra("medName", item.medName)
            }
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        // 行リストの件数（データセットのサイズ）を戻り値にセットする
        return dataset.size
    }

}
package com.example.medmemo

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

open class OverflowMenu : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //　インフレータにオーバーフローメニューのデザインを設定する
        menuInflater.inflate(R.menu.menu_item, menu)
        // 戻り値にtrueをセットする
        return true
    }
    // メニュー選択時
    companion object {
        fun handleMenuItemSelected(activity: AppCompatActivity, item: MenuItem): Boolean {
          return when (item.itemId) {
              // ホームの時
              R.id.home -> {
                  val intent = Intent(activity, MedicineActivity::class.java)
                  activity.startActivity(intent)
                  true
              }
              //　薬の追加の時
              R.id.medaddition -> {
                  val  intent = Intent(activity,MedAddActivity::class.java)
                  activity.startActivity(intent)
                  true
              }
              // ログアウトの時
              R.id.logout -> {
                  MyApplication.getInstance().loginUserId = ""
                  val intent = Intent(activity, LoginActivity::class.java)

                  intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                  activity.startActivity(intent)
                  activity.finish()
                  true
              }
              else -> activity.onOptionsItemSelected(item)
          }
        }
    }
}
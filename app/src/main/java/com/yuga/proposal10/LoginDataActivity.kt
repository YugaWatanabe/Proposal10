package com.yuga.proposal10

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LoginDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_data)

        val backButton = findViewById<Button>(R.id.backMain2)

        val syoriBCounts = findViewById<TextView>(R.id.syoriBCount)
        val syoriBAverages = findViewById<TextView>(R.id.syoriBAve)
        val syoriBMins = findViewById<TextView>(R.id.syoriBMin)
        val syoriBMaxs = findViewById<TextView>(R.id.syoriBMax)

        val syoriBTotalAves = findViewById<TextView>(R.id.syoriBTotalAve)
        val syoriBTotalMins = findViewById<TextView>(R.id.syoriBTotalMin)
        val syoriBTotalMaxs = findViewById<TextView>(R.id.syoriBTotalMax)
        val syoriBPercentageAves = findViewById<TextView>(R.id.syoriBPercentageAve)
        val syoriBPercentageMins = findViewById<TextView>(R.id.syoriBPercentageMin)
        val syoriBPercentageMaxs = findViewById<TextView>(R.id.syoriBPercentageMax)
        val decryptTimeAves = findViewById<TextView>(R.id.decryptTimeAve)

        val timeStore: SharedPreferences = getSharedPreferences("TimeStore", Context.MODE_PRIVATE)

        backButton.setOnClickListener{
            finish()
        }


        // なにもデータがない場合は0の割り算になってしまうので、1で初期化
        var loginCounts = 1
        var loginScore = 1L
        var loginBeforeScore = 1L

        var loginBeforeMin = 1000000000L
        var loginBeforeMax = 0L

        var totalSum = 1L
        var totalMin = Long.MAX_VALUE
        var totalMax = 0L
        var totalAve = 1L

        var loginPercentMin = 0f
        var loginPercentMax = 0f

        var decryptTimeSum = 1L
        var decryptTimeAve = 0L

        // 保存した処理Aの回数とか時間のデータを引き出す
        if(timeStore.getInt("loginCount" , 0) != 0){
            loginCounts = timeStore.getInt("loginCount" , 0)
            loginBeforeScore = timeStore.getLong("loginScore", 0)

            loginBeforeMin = timeStore.getLong("loginMin", 1000000000L)
            loginBeforeMax = timeStore.getLong("loginMax", 0)

            // トータル時間関連
            totalSum = timeStore.getLong("decryptProcessTimeSum", 0)
            totalMin = timeStore.getLong("decryptProcessTimeMin", 9223372036854775806L)
            totalMax = timeStore.getLong("decryptProcessTimeMax", 0L)

            loginPercentMin = timeStore.getFloat("loginPercentMin", Float.MAX_VALUE)
            loginPercentMax = timeStore.getFloat("loginPercentMax", Float.MIN_VALUE)

            decryptTimeSum = timeStore.getLong("decryptTimeSum", 0L)
        }


        //
        loginScore = loginBeforeScore / loginCounts
        totalAve = totalSum / loginCounts
        decryptTimeAve = decryptTimeSum / loginCounts
        val percentAve = (loginBeforeScore.toFloat() / totalSum.toFloat()) * 100

        // 何もデータがない場合に0回と表示するため
        if(totalAve == 1L) {
            totalAve = 0L
            totalMin = 0L
        }

        // 何もデータがない場合に0回と表示するため
        if(decryptTimeAve == 1L) {
            decryptTimeAve = 0L
        }

        // 何もデータがない場合に0回と表示するため
        if(loginScore == 1L){
            loginCounts = 0
            loginScore = 0
            loginBeforeMin = 0L
        }

        syoriBCounts.text = loginCounts.toString() + "回"
        syoriBAverages.text = loginScore.toString() + " (ns)"

        syoriBMins.text = loginBeforeMin.toString() + " (ns)"
        syoriBMaxs.text = loginBeforeMax.toString() + " (ns)"

        syoriBTotalAves.text = totalAve.toString() + " (ns)"
        syoriBTotalMins.text = totalMin.toString() + " (ns)"
        syoriBTotalMaxs.text = totalMax.toString() + " (ns)"

        syoriBPercentageAves.text = percentAve.toString() + " %"
        syoriBPercentageMins.text = loginPercentMin.toString() + " %"
        syoriBPercentageMaxs.text = loginPercentMax.toString() + " %"

        decryptTimeAves.text = decryptTimeAve.toString() + " (ns)"
    }
}
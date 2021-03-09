package com.yuga.proposal10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Context
import android.widget.Button
import android.widget.TextView
import android.content.SharedPreferences;

class RegisterDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_data)

        val backButton = findViewById<Button>(R.id.backMain2)

        val syoriACounts = findViewById<TextView>(R.id.syoriBCount)
        val syoriAAverages = findViewById<TextView>(R.id.syoriBAve)
        val syoriAMins = findViewById<TextView>(R.id.syoriBMin)
        val syoriAMaxs = findViewById<TextView>(R.id.syoriBMax)

        val syoriATotalAves = findViewById<TextView>(R.id.syoriBTotalAve)
        val syoriATotalMins = findViewById<TextView>(R.id.syoriBTotalMin)
        val syoriATotalMaxs = findViewById<TextView>(R.id.syoriBTotalMax)
        val syoriAPercentageAves = findViewById<TextView>(R.id.syoriBPercentageAve)
        val syoriAPercentageMins = findViewById<TextView>(R.id.syoriBPercentageMin)
        val syoriAPercentageMaxs = findViewById<TextView>(R.id.syoriBPercentageMax)
        val encryptTimeAves = findViewById<TextView>(R.id.decryptTimeAve)

        val timeStore: SharedPreferences = getSharedPreferences("TimeStore", Context.MODE_PRIVATE)

        backButton.setOnClickListener{
            finish()
        }


        // なにもデータがない場合は0の割り算になってしまうので、1で初期化
        var regCounts = 1
        var accountScore = 1L
        var beforeScore = 1L

        var regBeforeMin = 1000000000L
        var regBeforeMax = 0L

        var totalSum = 1L
        var totalMin = Long.MAX_VALUE
        var totalMax = 0L
        var totalAve = 1L

        var regPercentMin = 0f
        var regPercentMax = 0f

        var encryptTimeSum = 1L
        var encryptTimeAve = 0L

        // 保存した処理Aの回数とか時間のデータを引き出す
        if(timeStore.getInt("regCount" , 0) != 0){
            regCounts = timeStore.getInt("regCount" , 0)
            beforeScore = timeStore.getLong("regScore", 0)

            regBeforeMin = timeStore.getLong("regMin", Long.MAX_VALUE)
            regBeforeMax = timeStore.getLong("regMax", 0)

            // トータル時間関連
            totalSum = timeStore.getLong("cryptProcessTimeSum", 0)
            totalMin = timeStore.getLong("cryptProcessTimeMin", Long.MAX_VALUE)
            totalMax = timeStore.getLong("cryptProcessTimeMax", 0L)

            regPercentMin = timeStore.getFloat("regPercentMin", Float.MAX_VALUE)
            regPercentMax = timeStore.getFloat("regPercentMax", Float.MIN_VALUE)

            encryptTimeSum = timeStore.getLong("encryptTimeSum", 0L)
        }


        // 今までのアカウント作成内部処理にかかった時間の平均
        accountScore = beforeScore / regCounts
        totalAve = totalSum / regCounts
        encryptTimeAve = encryptTimeSum / regCounts
        val percentAve = (beforeScore.toFloat() / totalSum.toFloat()) * 100

        // 何もデータがない場合に0回と表示するため
        if(totalAve == 1L) {
            totalAve = 0L
            totalMin = 0L
        }

        // 何もデータがない場合に0回と表示するため
        if(encryptTimeAve == 1L) {
            encryptTimeAve = 0L
        }

        // 何もデータがない場合に0回と表示するため
        if(accountScore == 1L){
            regCounts = 0
            accountScore = 0
            regBeforeMin = 0L
        }

        syoriACounts.text = regCounts.toString() + "回"
        syoriAAverages.text = accountScore.toString() + " (ns)"

        syoriAMins.text = regBeforeMin.toString() + " (ns)"
        syoriAMaxs.text = regBeforeMax.toString() + " (ns)"

        syoriATotalAves.text = totalAve.toString() + " (ns)"
        syoriATotalMins.text = totalMin.toString() + " (ns)"
        syoriATotalMaxs.text = totalMax.toString() + " (ns)"

        syoriAPercentageAves.text = percentAve.toString() + " %"
        syoriAPercentageMins.text = regPercentMin.toString() + " %"
        syoriAPercentageMaxs.text = regPercentMax.toString() + " %"

        encryptTimeAves.text = encryptTimeAve.toString() + " (ns)"

    }



    }

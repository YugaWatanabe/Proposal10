package com.yuga.proposal10

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences;

import android.util.Base64
import android.view.View
import android.widget.*
import java.security.MessageDigest

class RegisterProcessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_process)

        // トータル時間計測開始
        var totalStartTime = System.nanoTime()


        val username = findViewById<EditText>(R.id.username_text2)
        val password = findViewById<EditText>(R.id.password_text2)
        val backLogin = findViewById<Button>(R.id.login_back)
        val send = findViewById<Button>(R.id.send2)

        val randStr = findViewById<TextView>(R.id.random_str)
        val randCrypt = findViewById<TextView>(R.id.random_cry)
        val timeScore = findViewById<TextView>(R.id.timeView)
        val hashPwText = findViewById<TextView>(R.id.register_hpw)
        val totalTimeText = findViewById<TextView>(R.id.registerTotalTime)


        //"timeStore"は時間計測用に一時的に時間を保存するところ
        val timeStore: SharedPreferences = getSharedPreferences("TimeStore", Context.MODE_PRIVATE)


        var spinnerSite = findViewById<Spinner>(R.id.regSpinner)
        val items = resources.getStringArray(R.array.user_items)

        val Adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)


        //スピナーにアダプターを設定
        spinnerSite.adapter = Adapter

        // dataStoreNameは暗号化乱数を保存するファイルの名前としてのちに使う。初期化しておく
        var dataStoreName = "DataStores"

        spinnerSite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
            ) {
                //選択されたURLをdataStoreNameに格納
                dataStoreName = parent.getItemAtPosition(position) as String;
                // dataStoreName = MessageDigest.getInstance("MD5").digest(dataStoreName.toByteArray()).joinToString(separator = ""){
                //    "%02x".format(it)
                // }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)


        send.setOnClickListener{
            // 計測スタート用
            val start = System.nanoTime()
            //乱数を生成
            val rand = RandCreate.randomStr()
            //乱数を暗号化

            val startRandTime = System.nanoTime()
            val randPair = Cryption().encryptRand(rand)
            val endRandTime = System.nanoTime()

            val editor = dataStore.edit()

            var userIndex = username.getText().toString() + dataStoreName
            userIndex = MessageDigest.getInstance("MD5").digest(userIndex.toByteArray()).joinToString(separator = "") {
                "%02x".format(it)
            }

            //前半がキー、後半が乱数としてペアで保存。今はとりあえずユーザ名を鍵に。のちにサイト名とかAliasとかにするかも
            editor.putString(userIndex + ".iv",  Base64.encodeToString(randPair.first, Base64.DEFAULT))
            editor.putString(userIndex + ".pw",  Base64.encodeToString(randPair.second, Base64.DEFAULT))

            editor.commit()
            // 非同期があればeditor.apply()

            // 乱数とパスワードでハッシュをとる
            val str = rand + password.getText().toString()
            var strHash = MessageDigest.getInstance("SHA-256").digest(str.toByteArray()).joinToString(separator = "") {
                "%02x".format(it)
            }


            val end = System.nanoTime()
            val time = end - start

            val totalTime = end - totalStartTime

            val encryptTime = endRandTime - startRandTime

            // クリップボードにハッシュドパスワードをコピー
            val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val myClip: ClipData? = ClipData.newPlainText("", strHash)

            if (myClipboard != null) {
                if (myClip != null) {
                    myClipboard.setPrimaryClip(myClip)

                }
            }

            Toast.makeText(applicationContext, "ハッシュドパスワードをコピー", Toast.LENGTH_LONG).show()

            // 計測した値を保存とかする
            var beforeCount = 0
            var beforeScore = 0L
            var beforeMin = time
            var beforeMax = time

            //トータル時間など
            var beforeTotalSum = 0L
            var beforeTotalMin = totalTime
            var beforeTotalMax = totalTime

            // 割合
            var regPercent = (time.toFloat() / totalTime.toFloat()) * 100
            var regPercentMin = regPercent
            var regPercentMax = regPercent

            // 暗号化処理の時間
            var encryptTimeSum = 0L

            if(timeStore.getInt("regCount" , 0) != 0){
                beforeCount = timeStore.getInt("regCount" , 0)
                beforeScore = timeStore.getLong("regScore", 0)

                beforeMin = timeStore.getLong("regMin", 90000000000000L)
                beforeMax = timeStore.getLong("regMax", 0)

                if(time < beforeMin){
                    beforeMin = time
                }
                if(beforeMax < time){
                    beforeMax = time
                }

                // トータルにかかった時間関連
                beforeTotalSum = timeStore.getLong("cryptProcessTimeSum", 0)
                beforeTotalMin = timeStore.getLong("cryptProcessTimeMin", 90000000000000L)
                beforeTotalMax = timeStore.getLong("cryptProcessTimeMax", 0L)

                if(totalTime < beforeTotalMin){
                    beforeTotalMin = totalTime
                }
                if(beforeTotalMax < totalTime){
                    beforeTotalMax = totalTime
                }

                // 処理Aが占める割合の関連
                regPercentMin = timeStore.getFloat("regPercentMin", 1000.000000f)
                regPercentMax = timeStore.getFloat("regPercentMax", Float.MIN_VALUE)

                if (regPercent < regPercentMin){
                    regPercentMin = regPercent
                }
                if (regPercentMax < regPercent){
                    regPercentMax = regPercent
                }

                // 暗号化にかかる時間関連
                encryptTimeSum = timeStore.getLong("encryptTimeSum", 0L)
            }

            val timer =  timeStore.edit()
            timer.putInt("regCount", beforeCount + 1)
            timer.putLong("regScore", beforeScore + time)

            timer.putLong("regMin", beforeMin)
            timer.putLong("regMax", beforeMax)

            // トータルにかかった時間を合計して保存
            timer.putLong("cryptProcessTimeSum", beforeTotalSum + totalTime)

            timer.putLong("cryptProcessTimeMin", beforeTotalMin)
            timer.putLong("cryptProcessTimeMax", beforeTotalMax)

            // 割合データの保存
            timer.putFloat("regPercentMin", regPercentMin)
            timer.putFloat("regPercentMax", regPercentMax)

            //暗号化時間を保存
            timer.putLong("encryptTimeSum", encryptTimeSum + encryptTime)

            timer.commit()

            //表示用（テスト確認用）

            randStr.text = rand
            randCrypt.text = Base64.encodeToString(randPair.second, Base64.DEFAULT)
            hashPwText.text = strHash
            timeScore.text = time.toString() + " (ns)"
            totalTimeText.text = totalTime.toString() + " (ns)"



            //timeEditers.getString()

            //if(timeEditer.getString("recounts", null) != null) {

            //   val regCount = timeEditer.getString("regcounts", 1)!!

            //}

            // randCrypt.text = Cryption().decryptData(randPair.first, randPair.second)

            //randStr.text = rand
            //randCrypt.text = byteArrayToHexString(randPair.first + randPair.second)
        }

        backLogin.setOnClickListener {
            finish()
        }

    }


    fun byteToHexString(byte: Int) = "%02X".format(byte.toByte())

    fun byteArrayToHexString(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (byte in bytes) {
            builder.append(byteToHexString(byte.toInt()))
        }
        return builder.toString()
    }
}
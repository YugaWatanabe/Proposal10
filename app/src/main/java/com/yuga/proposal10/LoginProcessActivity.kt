package com.yuga.proposal10

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Base64;
import android.view.View;
import android.widget.*;


import javax.crypto.*

import java.security.MessageDigest

class LoginProcessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_process)

        // トータル時間計測開始
        var totalStartTime = System.nanoTime()

        val timeStore: SharedPreferences = getSharedPreferences("TimeStore", Context.MODE_PRIVATE)
        val dataStore: SharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val timer =  timeStore.edit()

        //256bitの疑似乱数をつかう
        val rand = RandCreate.randomStr()
        val pair = Cryption().encryptRand(rand)
        val decryptedText = Cryption().decryptRand(pair.first, pair.second)

        val username = findViewById<EditText>(R.id.username_text)
        val password = findViewById<EditText>(R.id.password_text)

        val login = findViewById<Button>(R.id.startSyori2)
        val reg = findViewById<Button>(R.id.goSyoriDataB)

        val encrypted = findViewById<TextView>(R.id.encrypted)
        val decrypted = findViewById<TextView>(R.id.decrypted)
        val timeScore = findViewById<TextView>(R.id.timeView)
        val totalTimeScore = findViewById<TextView>(R.id.loginTotalTime)

        var spinnerSite = findViewById<Spinner>(R.id.spinner)
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
                //dataStoreName = MessageDigest.getInstance("MD5").digest(dataStoreName.toByteArray()).joinToString(separator = ""){
                //    "%02x".format(it)
                //}

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }




        //送信をクリックしたら
        login.setOnClickListener{


            val start = System.nanoTime()

            val randIv : String
            val randPw : String

            var userIndex = username.getText().toString() + dataStoreName
            userIndex = MessageDigest.getInstance("MD5").digest(userIndex.toByteArray()).joinToString(separator = "") {
                "%02x".format(it)
            }



            //SharedPreferencesから取り出す。String?になっちゃうので、ifでスコープを限定する必要がある
            if(dataStore.getString(userIndex + ".iv", null) != null && dataStore.getString(userIndex + ".pw", null) != null){

                val startRandTime = System.nanoTime()
                //暗号化した情報を取り出す
                randIv = dataStore.getString(userIndex + ".iv", null)!!
                randPw = dataStore.getString(userIndex + ".pw", null)!!

                val randPair: Pair<ByteArray, ByteArray> = Pair(Base64.decode(randIv, Base64.DEFAULT), Base64.decode(randPw, Base64.DEFAULT))
                val decryptedRand = Cryption().decryptRand(randPair.first, randPair.second)

                val endRandTime = System.nanoTime()
                encrypted.text = decryptedRand

                // 乱数とパスワードでハッシュをとる
                val str = decryptedRand + password.getText().toString()
                var strHash = MessageDigest.getInstance("SHA-256").digest(str.toByteArray()).joinToString(separator = "") {
                    "%02x".format(it)
                }



                val end = System.nanoTime()
                // 処理にかかった時間
                val time = end - start
                // この画面にうつってから、処理が始まるまで
                val totalTime = start - totalStartTime

                // クリップボードにハッシュドパスワードをコピー
                val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                val myClip: ClipData? = ClipData.newPlainText("", strHash)

                if (myClipboard != null) {
                    if (myClip != null) {
                        myClipboard.setPrimaryClip(myClip)

                    }
                }

                Toast.makeText(applicationContext, "ハッシュドパスワードをコピー", Toast.LENGTH_LONG).show()

                // 計測した処理時間などを保存とかする
                var beforeCount = 0
                var beforeScore = 0L
                var beforeMin = time
                var beforeMax = time

                //トータル時間など
                var beforeTotalSum = 0L
                var beforeTotalMin = totalTime
                var beforeTotalMax = totalTime

                // 割合
                var loginPercent = (time.toFloat() / totalTime.toFloat()) * 100
                var loginPercentMin = loginPercent
                var loginPercentMax = loginPercent

                // 暗号化処理の時間
                var decryptTimeSum = 0L

                if(timeStore.getInt("loginCount" , 0) != 0){
                    beforeCount = timeStore.getInt("loginCount" , 0)
                    beforeScore = timeStore.getLong("loginScore", 0)

                    beforeMin = timeStore.getLong("loginMin", 9223372036854775806L)
                    beforeMax = timeStore.getLong("loginMax", 0)

                    if(time < beforeMin){
                        beforeMin = time
                    }
                    if(beforeMax < time){
                        beforeMax = time
                    }

                    // トータルにかかった時間関連
                    beforeTotalSum = timeStore.getLong("decryptProcessTimeSum", 0)
                    beforeTotalMin = timeStore.getLong("decryptProcessTimeMin", 9223372036854775806L)
                    beforeTotalMax = timeStore.getLong("decryptProcessTimeMax", 0L)

                    if(totalTime < beforeTotalMin){
                        beforeTotalMin = totalTime
                    }
                    if(beforeTotalMax < totalTime){
                        beforeTotalMax = totalTime
                    }

                    // 処理Aが占める割合の関連
                    loginPercentMin = timeStore.getFloat("loginPercentMin", 1000.000000f)
                    loginPercentMax = timeStore.getFloat("loginPercentMax", Float.MIN_VALUE)

                    if (loginPercent < loginPercentMin){
                        loginPercentMin = loginPercent
                    }
                    if (loginPercentMax < loginPercent){
                        loginPercentMax = loginPercent
                    }

                    // 復号にかかる時間関連
                    decryptTimeSum = timeStore.getLong("decryptTimeSum", 0L)

                }


                timer.putInt("loginCount", beforeCount + 1)
                timer.putLong("loginScore", beforeScore + time)

                timer.putLong("loginMin", beforeMin)
                timer.putLong("loginMax", beforeMax)

                // トータルにかかった時間を合計して保存
                timer.putLong("decryptProcessTimeSum", beforeTotalSum + totalTime)

                timer.putLong("decryptProcessTimeMin", beforeTotalMin)
                timer.putLong("decryptProcessTimeMax", beforeTotalMax)

                // 割合データの保存
                timer.putFloat("loginPercentMin", loginPercentMin)
                timer.putFloat("loginPercentMax", loginPercentMax)

                //暗号化時間を保存
                timer.putLong("decryptTimeSum", decryptTimeSum + (endRandTime - startRandTime))

                timer.commit()

                decrypted.text = strHash
                timeScore.text = time.toString() + " (ns)"
                totalTimeScore.text = totalTime.toString() + " (ns)"

                // 仮に連続で試行されたときの対策で、いちおうやっておく
                totalStartTime = System.nanoTime()

            }else{
                encrypted.text = "入力が正しいかチェックして"
                decrypted.text = "乱数取り出しがうまくできなかった"
            }


        }

        //画面遷移用
        reg.setOnClickListener {
            finish()
        }


    }

    fun hexStringToByte(hexString: String) = hexString.toInt(16).toByte()

    fun hexStringToByteArray(hexString: String): ByteArray {
        val array = ByteArray(hexString.length / 2)
        for (index in 0 until array.count()) {
            val pointer = index * 2
            array[index] = hexStringToByte(hexString.substring(pointer, pointer + 2))
        }
        return array
    }


    //暗号化した文字列を表示するため用（テスト用）
    fun byteToHexString(byte: Int) = "%02X".format(byte.toByte())

    fun byteArrayToHexString(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (byte in bytes) {
            builder.append(byteToHexString(byte.toInt()))
        }
        return builder.toString()
    }



}

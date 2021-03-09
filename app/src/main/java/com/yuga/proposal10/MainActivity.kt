package com.yuga.proposal10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.*
import android.os.Build
import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.StrongBoxUnavailableException

import android.widget.*;
import androidx.annotation.RequiresApi

import javax.crypto.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timeStore: SharedPreferences = getSharedPreferences("TimeStore", Context.MODE_PRIVATE)
        val timer =  timeStore.edit()

        val goSyoriA = findViewById<Button>(R.id.goSyoriA)
        val goSyoriB = findViewById<Button>(R.id.goSyoriB)

        val goSyoriDataA = findViewById<Button>(R.id.goSyoriDataA)
        val goSyoriDataB = findViewById<Button>(R.id.goSyoriDataB)


        if(timeStore.getInt("initState", 0) == 0){
            val keyAlias = "aaaaa2"

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")


            if(Build.VERSION.SDK_INT <= 27){
                var keyGenParameterSpec = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build()

                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()
            }else if(Build.VERSION.SDK_INT >= 28){
                try{
                    var keyGenParameterSpec = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setIsStrongBoxBacked(true).build()
                    keyGenerator.init(keyGenParameterSpec)
                    keyGenerator.generateKey()
                }catch (e: StrongBoxUnavailableException){
                    var keyGenParameterSpec = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build()

                    keyGenerator.init(keyGenParameterSpec)
                    keyGenerator.generateKey()
                }

            }




            timer.putInt("initState", 1)
            timer.commit()
        }




        goSyoriA.setOnClickListener {
            val intent = Intent(this, RegisterProcessActivity::class.java)
            startActivity(intent)
        }

        goSyoriB.setOnClickListener {
            val intent = Intent(this, LoginProcessActivity::class.java)
            startActivity(intent)
        }

        goSyoriDataA.setOnClickListener {
            val intent = Intent(this, RegisterDataActivity::class.java)
            startActivity(intent)
        }

        goSyoriDataB.setOnClickListener {
            val intent = Intent(this, LoginDataActivity::class.java)
            startActivity(intent)
        }


    }
}
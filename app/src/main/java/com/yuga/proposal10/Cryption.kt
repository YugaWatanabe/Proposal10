package com.yuga.proposal10

import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

public class Cryption {

    fun getkey(): SecretKey {
        val keystore = KeyStore.getInstance("AndroidKeyStore")
        keystore.load(null)

        //Aliasはアプリ内しか使えないようになってるから、ここに書いちゃってもいい
        val secretKeyEntry = keystore.getEntry("aaaaa2", null) as KeyStore.SecretKeyEntry
        return secretKeyEntry.secretKey
    }

    //暗号化用
    fun encryptRand(data: String): Pair<ByteArray, ByteArray>{
        //今回パディングはなしにした
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        var tempData = data
        while (tempData.toByteArray().size % 16 != 0)
            tempData += "\u0020"

        cipher.init(Cipher.ENCRYPT_MODE, getkey())

        return Pair(cipher.iv, cipher.doFinal(tempData.toByteArray(Charsets.UTF_8)))
    }

    //復号化用
    fun decryptRand(ivBytes: ByteArray, data: ByteArray): String{
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")

        cipher.init(Cipher.DECRYPT_MODE, getkey(), IvParameterSpec(ivBytes))
        return cipher.doFinal(data).toString(Charsets.UTF_8).trim()
    }
}
package com.yuga.proposal10;


import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

//疑似乱数ジェネレーターをつかう
public class RandCreate {

    public static String randomStr(){
       // try{
            SecureRandom number = new SecureRandom();

            // Sha1PRNGは非推奨。これしか使えない場合以外はNativePRNGなどに変える。
            //number = SecureRandom.getInstance("NativePRNG");

            // 指定したバイト数の乱数はnextBytesで取得できる
            byte bytes[] = new byte[32];
            //ランダムなバイト列を生成
            number.nextBytes(bytes);

            //16新数の文字列にする
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                buffer.append(String.format("%02x", bytes[i]));
            }

            String randStr = String.valueOf(buffer);

            return randStr;
        //}catch (NoSuchAlgorithmException e){
       //     e.printStackTrace();
       // }

     //   return null;
    }
}

package com.amour.shop.classes;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class JavaApplication1 {


    public static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                +"lmnopqrstuvwxyz+/";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String Encrypt(String text)
            throws Exception {
        String ivkey="CDDAMQOTMYIAZEPQ";
        String key = "h3tTWAYJ55EGWMgZFs5gW5mquCIsgLhE";
      //  String key = generateRandomPassword(32);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes= new byte[32];
        byte[] b= key.getBytes("UTF-8");
        int len= b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);

        byte[] ivBytes= new byte[16];
        b= ivkey.getBytes("UTF-8");
        len= b.length;
        if (len > ivBytes.length) len = ivBytes.length;
        System.arraycopy(b, 0, ivBytes, 0, len);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);

        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        String EncryptedString= Base64.getEncoder().encodeToString(results);
        return EncryptedString.substring(0, EncryptedString.length()/2  )
                + key
                + EncryptedString.substring(EncryptedString.length()/2  );
    }




    /**
     * @param args the command line arguments
     */
    @SuppressLint("NewApi")
    public static void main(String[] args)  {

        CardInformation c = new CardInformation();
        c.number="0123456789123456";
        c.month="12";
        c.year="22";
        c.securityCode="123";

        Gson gson = new Gson();
        String json = gson.toJson(c);
        String EncryptedString = null;
        try {
            EncryptedString = Encrypt(json);System.out.println("Log [EncryptedString]:"+EncryptedString);
            System.out.println("Log [json]:"+json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}

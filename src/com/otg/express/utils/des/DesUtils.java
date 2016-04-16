package com.otg.express.utils.des;

import android.content.Context;

import javax.crypto.*;
import java.io.*;
import java.security.Key;
import java.security.SecureRandom;

public class DesUtils {

    public static void saveDesKey(Context context) {
        try {
            SecureRandom sr = new SecureRandom();
            // 为我们选择的DES算法生成一个KeyGenerator对象
            KeyGenerator kg = KeyGenerator.getInstance("DES");
            kg.init(sr);
            FileOutputStream fos = new FileOutputStream(context.getFilesDir()+"/crypt_key.xml");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            // 生成密钥
            Key key = kg.generateKey();
            oos.writeObject(key);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Key getKey(Context context) {
        File file = new File(context.getFilesDir()+"/crypt_key.xml");
        if(file.exists()) {
            Key kp = null;
            try {
                FileInputStream is = new FileInputStream(context.getFilesDir()+"/crypt_key.xml");
                ObjectInputStream oos = new ObjectInputStream(is);
                kp = (Key) oos.readObject();
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return kp;
        }
        return null;
    }

    public static void encrypt(String file, String dest,Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(dest);
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
        }
        cis.close();
        is.close();
        out.close();
    }


    public static byte[] encrypt(byte[] pwd,Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(pwd);
    }

    public static byte[] decrypt(byte[] dest,Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(dest);
    }
}

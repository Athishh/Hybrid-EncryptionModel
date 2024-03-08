package me.shifuu.baphomet.decryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

import static me.shifuu.baphomet.Baphomet.IV;

/**
 * @Author: Athishh
 * Package: me.shifuu.baphomet.decryption
 * Created on: 2/10/2024
 */
public class DecryptAES {

    // decrypt using AES
    public static byte[] decrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
        return cipher.doFinal(input);
    }
}

package me.shifuu.baphomet.decryption;

import javax.crypto.Cipher;
import java.security.Key;

/**
 * @Author: Athishh
 * Package: me.shifuu.baphomet.decryption
 * Created on: 2/10/2024
 */
public class DecryptRSA {

    public static byte[] decryptKey(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }
}

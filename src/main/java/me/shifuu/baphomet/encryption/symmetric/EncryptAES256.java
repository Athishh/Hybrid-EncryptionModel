package me.shifuu.baphomet.encryption.symmetric;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static me.shifuu.baphomet.Baphomet.IV;

/**
 * @Author: Athishh
 * Package: me.shifuu.baphomet.encryption.symmetric
 * Created on: 2/10/2024
 */
public class EncryptAES256 {

    public static SecretKey generateAES256Key() throws NoSuchAlgorithmException {
        // Generate a random symmetric key for AES encryption (yes for a minecraft plugin)
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    // encrypt using AES
    public static byte[] encrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
        return cipher.doFinal(input);
    }
}

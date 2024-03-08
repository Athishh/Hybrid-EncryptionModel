package me.shifuu.baphomet;

import me.shifuu.baphomet.decryption.DecryptAES;
import me.shifuu.baphomet.decryption.DecryptRSA;
import me.shifuu.baphomet.encryption.asymmetric.EncryptRSA;
import me.shifuu.baphomet.encryption.symmetric.EncryptAES256;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;
/**
 * @Author: Athishh
 * Package: me.shifuu.baphomet
 * Created on: 2/10/2024
 */
public class Baphomet {

    public static final byte[] IV = new byte[16];  // 128 bits

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path to the input file:");
        String inputPath = scanner.nextLine();
        File inputJar = new File(inputPath);

        System.out.println("Enter the path to the output file:");
        String outputPath = scanner.nextLine();
        File outputJar = new File(outputPath);

        // generate decrypted and encrypted jars for comparison
        Baphomet baphomet = new Baphomet();
        baphomet.encryptJar(inputJar, outputJar);
        File decryptedJar = new File(outputJar.getParent(), "decrypted_" + outputJar.getName());
        baphomet.decryptJar(outputJar, decryptedJar);
    }

    public void encryptJar(File inputJar, File outputJar) throws Exception {
        // get all the bytes of the jar file, separate classes and non-classes
        Map<String, byte[]> classBytes = JarUtil.loadJar(inputJar);
        Map<String, byte[]> nonClassEntries = JarUtil.loadNonClassEntries(inputJar);

        // generate aes256 key
        SecretKey secretKey = EncryptAES256.generateAES256Key();

       // use the key to encrypt all the class bytes
        classBytes.replaceAll((name, bytes) -> {
            try {
                return EncryptAES256.encrypt(bytes, secretKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // generate a public and private RSA key pair
        KeyPair keyPair = EncryptRSA.generateRSAKey();

        // encrypt the symmetric AES key using the public RSA key
        byte[] encryptedKey = EncryptRSA.encryptKey(secretKey.getEncoded(), keyPair.getPublic());

        // keep this saved for decryption later
        System.out.println("Encrypted AES key " + Base64.getEncoder().encodeToString(encryptedKey));
        System.out.println("Private RSA key " + Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

        // create the jar with the encrypted classes and non-class entries
        JarUtil.writeJar(outputJar, classBytes, nonClassEntries);
    }

    public void decryptJar(File inputJar, File outputJar) throws Exception {
        Map<String, byte[]> classBytes = JarUtil.loadJar(inputJar);
        Map<String, byte[]> nonClassEntries = JarUtil.loadNonClassEntries(inputJar);

        // enter our encrypted AES key and private RSA key
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter encrypted AES key");
        byte[] encryptedKey = Base64.getDecoder().decode(scanner.nextLine());
        System.out.println("Enter private RSA key");
        byte[] privateKeyBytes = Base64.getDecoder().decode(scanner.nextLine());

        // use private RSA key to decrypt the symmetric AES key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        byte[] keyBytes = DecryptRSA.decryptKey(encryptedKey, privateKey);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // decrypt the class files using the decrypted AES key
        classBytes.replaceAll((name, bytes) -> {
            try {
                return DecryptAES.decrypt(bytes, secretKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // create jar
        JarUtil.writeJar(outputJar, classBytes, nonClassEntries);
    }

}
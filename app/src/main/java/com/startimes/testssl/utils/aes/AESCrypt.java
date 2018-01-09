package com.startimes.testssl.utils.aes;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public final class AESCrypt {

    private static final String TAG = "AESCrypt";

    //AESCrypt-ObjC uses CBC and PKCS7Padding
    private static final String AES_MODE = "AES/CBC/PKCS7Padding";
    private static final String CHARSET = "UTF-8";

    //AESCrypt-ObjC uses SHA-256 (and so a 384-bit key)
    private static final String HASH_ALGORITHM = "SHA-384";

    //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
    private static final byte[] ivBytes = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};

    //togglable log option (please turn off in live!)
    public static boolean DEBUG_LOG_ENABLED = true;


    /**
     * Generates SHA256 hash of the password which is used as key
     *
     * @param password used to generated key
     * @return SHA256 of the password
     */
    public static SecretKeySpec generateKey(final String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();

        log("SHA key ", key);
        log("KEY ", unmaskKey(key));
        SecretKeySpec secretKeySpec = new SecretKeySpec(unmaskKey(key), "AES");
        return secretKeySpec;
    }

    private static byte[] unmaskKey(byte[] key) {

        if (key.length < 48 ){
            throw new RuntimeException("No decryption key available");
        }

        byte[] hmacSec=new byte[16];
        hmacSec[2]=(byte)(57);
        hmacSec[((hmacSec[2]&0xff)-44)]=(byte)(((hmacSec[2]&0xff)+106));
        hmacSec[((hmacSec[13]&0xff)-160)]=(byte)(((hmacSec[13]&0xff)+61));
        hmacSec[((hmacSec[13]&0xff)-163)]=(byte)(((hmacSec[2]&0xff)-34));
        hmacSec[((hmacSec[13]&0xff)-148)]=(byte)(((hmacSec[3]&0xff)-215));
        hmacSec[((hmacSec[2]&0xff)-51)]=(byte)(((hmacSec[3]&0xff)-215));
        hmacSec[((hmacSec[13]&0xff)-162)]=(byte)(((hmacSec[6]&0xff)+123));
        hmacSec[((hmacSec[0]&0xff)-15)]=(byte)(((hmacSec[1]&0xff)-52));
        hmacSec[((hmacSec[6]&0xff)-5)]=(byte)(((hmacSec[6]&0xff)+35));
        hmacSec[((hmacSec[8]&0xff)-66)]=(byte)(((hmacSec[2]&0xff)+109));
        hmacSec[((hmacSec[6]&0xff)-2)]=(byte)(((hmacSec[1]&0xff)-70));
        hmacSec[((hmacSec[4]&0xff)-33)]=(byte)(((hmacSec[0]&0xff)+208));
        hmacSec[((hmacSec[3]&0xff)-219)]=(byte)(((hmacSec[4]&0xff)+63));
        hmacSec[((hmacSec[11]&0xff)-222)]=(byte)(((hmacSec[8]&0xff)+137));
        hmacSec[((hmacSec[5]&0xff)-97)]=(byte)(((hmacSec[6]&0xff)+201));
        hmacSec[((hmacSec[3]&0xff)-212)]=(byte)(((hmacSec[0]&0xff)+48));

        SecretKey hmacKey = new SecretKeySpec(hmacSec, "HmacSHA256");

        Mac hmac = null;
        try {
            hmac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            hmac.init(hmacKey);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        byte[] randomValue = Arrays.copyOfRange(key, 16, 48);
        byte[] maskedKey = Arrays.copyOfRange(key, 0, 16);

        // Calculate key mask
        byte[] mask = hmac.doFinal(randomValue);

        // Unmask key
        for (int i= 0; i < 16; i++){
            maskedKey[i] = (byte)((byte)maskedKey[i]^(byte)mask[i+7]);
        }
        return maskedKey;
    }

    /**
     * AESEncrypt and encode message using 256-bit AES with key generated from password.
     *
     *
     * @param password used to generated key
     * @param message the thing you want to encrypt assumed String UTF-8
     *
     *
     *
     * @return Base64 encoded CipherText
     * @throws GeneralSecurityException if problems occur during encryption
     */
    public static String encrypt(final String password, String message)
            throws GeneralSecurityException {

        try {
            final SecretKeySpec key = generateKey(password);

            log("message", message);

            byte[] cipherText = encrypt(key, ivBytes, message.getBytes(CHARSET));

            //NO_WRAP is important as was getting \n at the end
            String encoded = Base64.encodeToString(cipherText, Base64.NO_WRAP);
            log("Base64.NO_WRAP", encoded);
            return encoded;
        } catch (UnsupportedEncodingException e) {
            if (DEBUG_LOG_ENABLED)
                Log.e(TAG, "UnsupportedEncodingException ", e);
            throw new GeneralSecurityException(e);
        }
    }


    /**
     * More flexible AES encrypt that doesn't encode
     * @param key AES key typically 128, 192 or 256 bit
     * @param iv Initiation Vector
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] encrypt(final SecretKeySpec key, final byte[] iv, final byte[] message)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherText = cipher.doFinal(message);
        log("cipherText", cipherText);
        return cipherText;
    }

    public static InputStream encryptFile(final SecretKeySpec key, final byte[] iv, File file)
            throws GeneralSecurityException {
        CipherInputStream cipherInputStream = null;
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        try {
            cipherInputStream = new CipherInputStream(
                    new FileInputStream(file), cipher);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        }
        return cipherInputStream;
    }

    /**
     * Decrypt and decode ciphertext using 256-bit AES with key generated from password
     *
     * @param password used to generated key
     * @param base64EncodedCipherText the encrpyted message encoded with base64
     * @return message in Plain text (String UTF-8)
     * @throws GeneralSecurityException if there's an issue decrypting
     */
    public static String decrypt(final String password, String base64EncodedCipherText)
            throws GeneralSecurityException {

        try {
            final SecretKeySpec key = generateKey(password);

            log("base64EncodedCipherText", base64EncodedCipherText);
            byte[] decodedCipherText = Base64.decode(base64EncodedCipherText, Base64.NO_WRAP);
            log("decodedCipherText", decodedCipherText);

            byte[] decryptedBytes = decrypt(key, ivBytes, decodedCipherText);

            log("decryptedBytes", decryptedBytes);
            String message = new String(decryptedBytes, CHARSET);
            log("message", message);

            return message;
        } catch (UnsupportedEncodingException e) {
            if (DEBUG_LOG_ENABLED)
                Log.e(TAG, "UnsupportedEncodingException ", e);

            throw new GeneralSecurityException(e);
        }
    }


    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key AES key typically 128, 192 or 256 bit
     * @param iv Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted message cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] decrypt(final SecretKeySpec key, final byte[] iv, final byte[] decodedCipherText)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(decodedCipherText);

        log("decryptedBytes", decryptedBytes);

        return decryptedBytes;
    }

    public static InputStream decryptFile(final SecretKeySpec key, final byte[] iv, InputStream inputStream)
            throws GeneralSecurityException {
        CipherInputStream cipherInputStream = null;
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        cipherInputStream = new CipherInputStream(
                inputStream, cipher);
        return cipherInputStream;
    }

    public static InputStream decryptFile(final SecretKeySpec key, final byte[] iv, File file)
            throws GeneralSecurityException {
        CipherInputStream cipherInputStream = null;
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        try {
            cipherInputStream = new CipherInputStream(
                    new FileInputStream(file), cipher);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        }
        return cipherInputStream;
    }

    public static void log(String what, byte[] bytes) {
        if (DEBUG_LOG_ENABLED)
            Log.e(TAG, what + "[" + bytes.length + "] [" + bytesToHex(bytes) + "]");
    }

    private static void log(String what, String value) {
        if (DEBUG_LOG_ENABLED)
            Log.e(TAG, what + "[" + value.length() + "] [" + value + "]");
    }

    /**
     * Converts byte array to hexidecimal useful for logging and fault finding
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private AESCrypt() {
    }
}

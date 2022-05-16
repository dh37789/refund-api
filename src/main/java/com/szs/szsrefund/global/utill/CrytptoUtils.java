package com.szs.szsrefund.global.utill;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CrytptoUtils {

    private CrytptoUtils() {}

    private static String key;
    private static String chiper;
    private static String iv;

    @Value("${crypto.aes256.key}")
    public void setKey(String key) {
        this.key = key;
    }

    @Value("${crypto.aes256.cipher}")
    public void setCipher(String chiper) {
        this.chiper = chiper;
    }

    @PostConstruct
    public void setIv() {
        this.iv = key.substring(0, 16);
    }

    public static String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(chiper);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(chiper);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }
}

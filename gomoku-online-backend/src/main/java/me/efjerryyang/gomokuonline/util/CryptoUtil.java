package me.efjerryyang.gomokuonline.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil {

    public static String generateKey() {
        try {
            // 创建一个AES加密算法的密钥生成器
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            // 指定密钥长度为256位
            keyGen.init(256);
            // 生成密钥
            SecretKey key = keyGen.generateKey();
            // 将密钥输出为byte数组形式
            byte[] keyBytes = key.getEncoded();
            // 将byte数组转换为Base64编码的字符串形式，以便于存储或传输
            return java.util.Base64.getEncoder().encodeToString(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}

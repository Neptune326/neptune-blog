package com.neptune.utils;

import com.neptune.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class PasswordUtils {

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // 哈希密码
    public static String hashPassword(String password, String salt) {
        String saltedPassword = salt + password;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new ApiException("生成密码失败");
        }
        if (md == null) {
            throw new ApiException("生成密码失败");
        }
        byte[] hashedPassword = md.digest(saltedPassword.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    // 验证密码
    public static boolean verify(String password, String salt, String hashedPassword) {
        String hashedInput = hashPassword(password, salt);
        return hashedInput.equals(hashedPassword);
    }

    public static void main(String[] args) {
        String salt = generateSalt();
        String password = hashPassword("123@admin", salt);
        log.info("管理员-salt：{}，password：{}", salt, password);
    }

}

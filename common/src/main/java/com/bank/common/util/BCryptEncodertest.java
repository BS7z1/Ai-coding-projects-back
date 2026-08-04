package com.bank.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptEncodertest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("123456");
        System.out.println("加密后的密文: " + encodedPassword);
        System.out.println("密文长度: " + encodedPassword.length());

        // 验证一下
        boolean matches = encoder.matches("123456", encodedPassword);
        System.out.println("验证结果: " + matches);
    }
}

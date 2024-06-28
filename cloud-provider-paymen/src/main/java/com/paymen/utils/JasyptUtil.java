package com.paymen.utils;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-28 16:21
 * @Version 1.0
 **/
public class JasyptUtil {

    public static void main(String[] args) {
        String account = "root";
        String password = "root";
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        //秘钥
        //encryptor.setPassword(System.getProperty("jasypt.encryptor.password"));
        encryptor.setPassword("csp");
        //密码进行加密
        String newAccount = encryptor.encrypt(account);
        String newPassword = encryptor.encrypt(password);
        System.out.println("加密后账号：" + newAccount);
        System.out.println("解密后账号：" + encryptor.decrypt(newAccount));
        System.out.println("加密后密码：" + newPassword);
        System.out.println("解密后密码：" + encryptor.decrypt(newPassword));
    }
}

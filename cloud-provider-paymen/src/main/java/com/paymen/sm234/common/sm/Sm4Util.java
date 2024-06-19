package com.paymen.sm234.common.sm;

/**
 * @description: 国密sm4工具类
 * 对key要求 ：128 bit = 128 / 8 = 16 byte
 * @author: huangzouqiang
 * @create: 2024-06-05 15:40
 * @Version 1.0
 **/

import cn.hutool.crypto.symmetric.SymmetricCrypto;

import java.nio.charset.StandardCharsets;

public class Sm4Util {
    /**
     * 加密
     *
     * @param key  秘钥
     * @param data 明文
     * @return 密文
     */
    public static String encrypt(String data, String key) {
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding", key.getBytes(StandardCharsets.UTF_8));
        return sm4.encryptHex(data).toUpperCase();
    }

    /**
     * 解密
     *
     * @param key  秘钥
     * @param data 密文
     * @return 明文
     */
    public static String decrypt(String data, String key) {
        SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding", key.getBytes(StandardCharsets.UTF_8));
        return sm4.decryptStr(data);
    }

    public static void main(String[] args) {
        String key = "GJwsXX_BzW=gJWJW";
        String data = "{\"name\":\"张三\",\"age\":32}";
        String encrypt = encrypt(data, key);
        String decrypt = decrypt(encrypt, key);
        System.err.println(encrypt);
        System.err.println(decrypt);
    }
}

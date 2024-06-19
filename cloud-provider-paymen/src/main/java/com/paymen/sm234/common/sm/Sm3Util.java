package com.paymen.sm234.common.sm;

/**
 * @description: Sm3加密工具类,单向的加密 ，用于验签的
 * @author: huangzouqiang
 * @create: 2024-06-05 15:39
 * @Version 1.0
 **/
import cn.hutool.crypto.SmUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Sm3Util {
    /**
     * 加密  自定义的key有要求的，暂时没处理
     *
     * @param key 秘钥
     * @param data 明文
     * @return 密文
     */
    public static String encrypt(String key, String data) {
        byte[] bytes = SmUtil.hmacSm3(Base64.getDecoder().decode(key))
                .digest(data);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 加密
     *
     * @param key 秘钥
     * @param data 明文
     * @return 密文
     */
    public static String encrypt2(String key, String data) {
        byte[] bytes = SmUtil.hmacSm3(key.getBytes(StandardCharsets.UTF_8))
                .digest(data);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * todo 自定义key加密有问题
     *
     * @param args
     */
    public static void main(String[] args) {
        String key = "GJwsXX_BzW=gJWJW";
//        String data = "{\"name\":\"张三\",\"age\":32}";
        String data = "{\"name\":\"张三\",\"age\":32}";
        String encrypt2 = encrypt2(key, data);
//        String encrypt1 = encrypt(key, data);
        System.err.println(encrypt2);
//        System.err.println(encrypt1);
    }
}

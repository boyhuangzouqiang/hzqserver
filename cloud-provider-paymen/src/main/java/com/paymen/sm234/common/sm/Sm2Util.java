package com.paymen.sm234.common.sm;

/**
 * @description:
 * @author: huangzouqiang
 * @create: 2024-06-05 15:39
 * @Version 1.0
 **/
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.util.HashMap;
import java.util.Map;

public class Sm2Util {
    /**
     * 生成秘钥对
     *
     * @return 公钥和私钥
     */
    public static Map<String, String> generateKeyPair() {
        SM2 sm2 = SmUtil.sm2();
        String publicKey = HexUtil.encodeHexStr(((BCECPublicKey) sm2.getPublicKey()).getQ().getEncoded(false)).toUpperCase();
        String privateKey = HexUtil.encodeHexStr(BCUtil.encodeECPrivateKey(sm2.getPrivateKey())).toUpperCase();
        return new HashMap<String, String>(2) {{
            put("publicKey", publicKey);
            put("privateKey", privateKey);
        }};
    }

    /**
     * 加密
     *
     * @param publicKey 公钥
     * @param data 明文
     * @return 密文
     */
    public static String encrypt(String publicKey, String data) {
        return SmUtil.sm2(null, publicKey)
                // 不写默认就是C1C3C2
                .setMode(SM2Engine.Mode.C1C3C2)
                .encryptHex(data.getBytes(), KeyType.PublicKey)
                // 加密后，密文前面会有04，需要去掉
                .substring(2);
    }

    /**
     * 解密
     *
     * @param privateKey 私钥
     * @param data 密文
     * @return 明文
     */
    public static String decrypt(String privateKey, String data) {
        // 确定前端不会加04，所以后端直接加（上面处理方式可能造成报错（Invalid point coordinates）：原因前端加密后密文自带04开头）
        data = "04" + data;
        return SmUtil.sm2(privateKey, null)
                // 不写默认就是C1C3C2
                .setMode(SM2Engine.Mode.C1C3C2)
                .decryptStr(data, KeyType.PrivateKey);
    }
}

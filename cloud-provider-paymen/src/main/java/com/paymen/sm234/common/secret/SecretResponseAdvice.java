package com.paymen.sm234.common.secret;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymen.sm234.common.base.ResponseBasic;
import com.paymen.sm234.common.base.SecretResponseBasic;
import com.paymen.sm234.common.sm.Sm3Util;
import com.paymen.sm234.common.sm.Sm4Util;
import com.paymen.sm234.common.utils.EncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static com.paymen.sm234.common.base.ResponseCode.SECRET_API_ERROR;


/**
 * @description:
 * @author: boykaff
 * @date: 2022-03-25-0025
 */
@Slf4j
@ControllerAdvice
public class SecretResponseAdvice implements ResponseBodyAdvice {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    /**
     * 使用AES base64加密返回
     *
     * @param o
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
//    @Override
//    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
//        // 判断是否需要加密
//        Boolean respSecret = SecretFilter.secretThreadLocal.get();
//        String secretKey = SecretFilter.clientPrivateKeyThreadLocal.get();
//        // 清理本地缓存
//        SecretFilter.secretThreadLocal.remove();
//        SecretFilter.clientPrivateKeyThreadLocal.remove();
//        if (null != respSecret && respSecret) {
//            if (o instanceof ResponseBasic) {
//                // 外层加密级异常
//                if (SECRET_API_ERROR == ((ResponseBasic) o).getCode()) {
//                    return SecretResponseBasic.fail(((ResponseBasic) o).getCode(), ((ResponseBasic) o).getData(), ((ResponseBasic) o).getMsg());
//                }
//                // 业务逻辑
//                try {
//                    // 使用FastJson序列号会导致和之前的接口响应参数不一致，后面会重点讲到
//                    String data = EncryptUtils.aesEncrypt(objectMapper.writeValueAsString(o), secretKey);
//                    // 增加签名
//                    long timestamp = System.currentTimeMillis() / 1000;
//                    int salt = EncryptUtils.genSalt();
//                    String dataNew = timestamp + "" + salt + "" + data + secretKey;
//                    String newSignature = Md5Utils.genSignature(dataNew);
//                    return SecretResponseBasic.success(data, timestamp, salt, newSignature);
//                } catch (Exception e) {
//                    logger.error("beforeBodyWrite error:", e);
//                    return SecretResponseBasic.fail(SECRET_API_ERROR, "", "服务端处理结果数据异常");
//                }
//            }
//        }
//        return o;
//    }


    /**
     * sm4 加密返回,sm3生成摘要 验签使用的
     *
     * @param o
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 判断是否需要加密
        Boolean respSecret = SecretFilter.secretThreadLocal.get();
        String secretKey = SecretFilter.clientPrivateKeyThreadLocal.get();
        // 清理本地缓存
        SecretFilter.secretThreadLocal.remove();
        SecretFilter.clientPrivateKeyThreadLocal.remove();
        if (null != respSecret && respSecret) {
            if (o instanceof ResponseBasic) {
                // 外层加密级异常
                if (SECRET_API_ERROR == ((ResponseBasic) o).getCode()) {
                    return SecretResponseBasic.fail(((ResponseBasic) o).getCode(), ((ResponseBasic) o).getData(), ((ResponseBasic) o).getMsg());
                }
                // 业务逻辑
                try {
                    String s = objectMapper.writeValueAsString(o);
                    log.info("加密前的返回数据:{}", s);
                    String data = Sm4Util.encrypt(s, secretKey);
                    // 增加签名
                    String newSignature = Sm3Util.encrypt2(secretKey, s);
                    long timestamp = System.currentTimeMillis() / 1000;
                    int salt = EncryptUtils.genSalt();
                    return SecretResponseBasic.success(data, timestamp, salt, newSignature);
                } catch (Exception e) {
                    log.error("beforeBodyWrite error:", e);
                    return SecretResponseBasic.fail(SECRET_API_ERROR, "", "服务端处理结果数据异常");
                }
            }
        }
        return o;
    }
}

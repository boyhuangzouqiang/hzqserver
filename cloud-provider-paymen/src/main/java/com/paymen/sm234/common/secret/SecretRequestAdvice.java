package com.paymen.sm234.common.secret;

import com.alibaba.fastjson2.JSON;
import com.paymen.sm234.common.base.ReqSecret;
import com.paymen.sm234.common.base.SecretHttpMessage;
import com.paymen.sm234.common.exceptions.ResultException;
import com.paymen.sm234.common.sm.Sm3Util;
import com.paymen.sm234.common.sm.Sm4Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Objects;

import static com.paymen.sm234.common.base.ResponseCode.SECRET_API_ERROR;


/**
 * @description: 该类只对post请求的@requestBody注解才生效，post没有任何参数的情况下不会进入该类
 * @author: boykaff
 * @date: 2022-03-25-0025
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class SecretRequestAdvice extends RequestBodyAdviceAdapter {
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        //如果支持加密消息，进行消息解密。
        String httpBody;
        if (Boolean.TRUE.equals(SecretFilter.secretThreadLocal.get())) {
            httpBody = decryptBody(inputMessage);
        } else {
            httpBody = StreamUtils.copyToString(inputMessage.getBody(), Charset.defaultCharset());
        }
        //返回处理后的消息体给messageConvert
        return new SecretHttpMessage(new ByteArrayInputStream(httpBody.getBytes()), inputMessage.getHeaders());
    }

    /**
     * 解密消息体
     * 使用的是md5验签、AES bas64解密
     *
     * @param inputMessage 消息体
     * @return 明文
     */
//    private String decryptBody(HttpInputMessage inputMessage) throws IOException {
//        InputStream encryptStream = inputMessage.getBody();
//        String requestBody = StreamUtils.copyToString(encryptStream, Charset.defaultCharset());
//        // 验签过程
//        HttpHeaders headers = inputMessage.getHeaders();
//        if (CollectionUtils.isEmpty(headers.get("clientType"))
//                || CollectionUtils.isEmpty(headers.get("timestamp"))
//                || CollectionUtils.isEmpty(headers.get("salt"))
//                || CollectionUtils.isEmpty(headers.get("signature"))) {
//            throw new ResultException(SECRET_API_ERROR, "请求解密参数错误，clientType、timestamp、salt、signature等参数传递是否正确传递");
//        }
//
//        String timestamp = String.valueOf(Objects.requireNonNull(headers.get("timestamp")).get(0));
//        String salt = String.valueOf(Objects.requireNonNull(headers.get("salt")).get(0));
//        String signature = String.valueOf(Objects.requireNonNull(headers.get("signature")).get(0));
//        String privateKey = SecretFilter.clientPrivateKeyThreadLocal.get();
//        ReqSecret reqSecret = JSON.parseObject(requestBody, ReqSecret.class);
//        String data = reqSecret.getData();
//        String newSignature = "";
//        if (!StringUtils.isEmpty(privateKey)) {
//            newSignature = Md5Utils.genSignature(timestamp + salt + data + privateKey);
//        }
//        if (!newSignature.equals(signature)) {
//            // 验签失败
//            throw new ResultException(SECRET_API_ERROR, "验签失败，请确认加密方式是否正确");
//        }
//
//        try {
//            String decrypt = EncryptUtils.aesDecrypt(data, privateKey);
//            if (StringUtils.isEmpty(decrypt)) {
//                decrypt = "{}";
//            }
//            return decrypt;
//        } catch (Exception e) {
//            log.error("error: ", e);
//        }
//        throw new ResultException(SECRET_API_ERROR, "解密失败");
//    }

    /**
     * sm4的解密  sm3验签
     * @param inputMessage
     * @return
     * @throws IOException
     */
    private String decryptBody(HttpInputMessage inputMessage) throws IOException {
        InputStream encryptStream = inputMessage.getBody();
        String requestBody = StreamUtils.copyToString(encryptStream, Charset.defaultCharset());
        // 验签过程
        HttpHeaders headers = inputMessage.getHeaders();
        String privateKey = SecretFilter.clientPrivateKeyThreadLocal.get();
        ReqSecret reqSecret = JSON.parseObject(requestBody, ReqSecret.class);
        String data = reqSecret.getData();
        try {
            //解密后的数据
            String decrypt = Sm4Util.decrypt(data, privateKey);
            if (StringUtils.isEmpty(decrypt)) {
                //todo 对数据的sm3的签名没有  或者约定一个特殊的签名,这里就不做签名验证了
                decrypt = "{}";
            } else {
                String signature = String.valueOf(Objects.requireNonNull(headers.get("signature")).get(0));
                if (CollectionUtils.isEmpty(headers.get("clientType"))) {
                    throw new ResultException(SECRET_API_ERROR, "请求解密参数错误，signature参数传递是否正确传递");
                }
                //sm3的签名
                String newSignature = Sm3Util.encrypt(privateKey, decrypt);
                if (!newSignature.equals(signature)) {
                    // 验签失败
                    throw new ResultException(SECRET_API_ERROR, "验签失败，请确认加密方式是否正确");
                }
            }
            return decrypt;
        } catch (Exception e) {
            log.error("error: ", e);
        }
        throw new ResultException(SECRET_API_ERROR, "解密失败");
    }
}

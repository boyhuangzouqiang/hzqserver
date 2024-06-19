package com.paymen.sm234.common.secret;

import com.paymen.sm234.common.constants.SecretConstant;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * @description: 过滤器
 * @author: boykaff
 * @date: 2022-03-25`
 */
public class SecretFilter implements Filter {
    public static ThreadLocal<Boolean> secretThreadLocal = new ThreadLocal<>();

    @Value("${secret.privateKey.default:GJwsXX_BzW=gJWJW}")
    private String defaultKey;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    private static final String key = "GJwsXX_BzW=gJWJW";

    public static ThreadLocal<String> clientPrivateKeyThreadLocal = new ThreadLocal<>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 免加密
        String requestURI = request.getRequestURI();
        requestURI = requestURI.replace(contextPath,"");
        //固定路由开头的接口  前端传递了签名信息  这两个情况的业务就走解密逻辑
        boolean signatureFlag = Objects.isNull(request.getHeader("signature"));
        if (!requestURI.startsWith(SecretConstant.PREFIX)) {
            secretThreadLocal.set(Boolean.FALSE);
            filterChain.doFilter(request, response);
        } else {
            // 加密，先只考虑POST情况 get请求不支持
            secretThreadLocal.set(Boolean.TRUE);
            // 简单获取对应加密的私钥 不通客户端对应的key
            String privateKey = ("WEB".equalsIgnoreCase(Objects.requireNonNull(request.getHeader("clientType")))) ? key : defaultKey;
            clientPrivateKeyThreadLocal.set(privateKey);
            filterChain.doFilter(request, response);
        }
    }
}

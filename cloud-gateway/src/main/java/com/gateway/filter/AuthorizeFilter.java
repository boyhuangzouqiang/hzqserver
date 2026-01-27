package com.gateway.filter;

import com.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @description: JWT令牌验证全局过滤器
 * 在网关层验证JWT令牌的有效性，验证通过后将用户信息添加到请求头中传递给下游服务
 * @Order值越小，该过滤器优先级越高，越先执行,请求进入网关会碰到三类过滤器： GatewayFilter，DefaultFilter，GloableFilter,
 * 如果三种过滤器的 order 值一样时，会按照 defaultFilter > 路由过滤器 > GlobalFilter 的顺序执行
 * <p>
 * Gateway网关的过滤器分为两种，一种是局部过滤器，一种是全局过滤器。
 * 过滤器，顾名思义，就是过滤一些请求，在这里，全局过滤器的作用是处理一切进入网关的请求和微服务响应，与GatewayFilter的作用一样。
 * 区别在于GatewayFilter通过配置定义，处理逻辑是固定的；而GlobalFilter的逻辑需要自己写代码实现
 * @author: huangzouqiang
 * @create: 2024-06-07 09:19
 * @Version 1.0
 **/
@Component
@Order(-1)
public class AuthorizeFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = getTokenFromRequest(request);

        // 如果请求路径是认证相关的，直接放行
        String path = request.getURI().getPath();
        if (isAuthPath(path)) {
            return chain.filter(exchange);
        }

        // 如果有token，则验证其有效性
        if (token != null && !token.isEmpty()) {
            if (jwtUtil.validateToken(token)) {
                // 从token中提取用户名，并将其添加到请求头中
                String username = jwtUtil.getUsernameFromToken(token);
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)  // 保留原始token
                        .header("X-User-Username", username)  // 添加用户名到请求头
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } else {
                // token无效，返回401未授权
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        } else {
            // 没有token但需要验证的路径，返回401未授权
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * 从请求中提取JWT令牌
     *
     * @param request HTTP请求
     * @return JWT令牌，如果不存在则返回null
     */
    private String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 判断是否为认证相关路径，不需要验证token
     *
     * @param path 请求路径
     * @return 是否为认证路径
     */
    private boolean isAuthPath(String path) {
        return path.startsWith("/auth/login") ||
                path.startsWith("/auth/register");
    }
}

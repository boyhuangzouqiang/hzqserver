package com.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @description: https://blog.csdn.net/m0_66532138/article/details/136480114
 * @Order值越小，该过滤器优先级越高，越先执行,请求进入网关会碰到三类过滤器： GatewayFilter，DefaultFilter，GloableFilter,如果三种过滤器的 order 值一样时，会按照 defaultFilter > 路由过滤器 > GlobalFilter 的顺序执行
 * <p>
 * Gateway网关的过滤器分为两种，一种是局部过滤器，一种是全局过滤器。
 * 过滤器，顾名思义，就是过滤一些请求，在这里，全局过滤器的作用是处理一切进入网关的请求和微服务响应，与GatewayFilter的作用一样。区别在于GatewayFilter通过配置定义，处理逻辑是固定的；而GlobalFilter的逻辑需要自己写代码实现
 * @author: huangzouqiang
 * @create: 2024-06-07 09:19
 * @Version 1.0
 **/
@Component
@Order(-1)
public class AuthorizeFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求参数
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        String authorization = queryParams.getFirst("authorization");
        //判断authorization值
        if ("admin".equals(authorization)) {
            //放行
            return chain.filter(exchange);
        }
        //拦截
        //设置状态码  401：未登录
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        //拦截请求
        return exchange.getResponse().setComplete();
    }
}

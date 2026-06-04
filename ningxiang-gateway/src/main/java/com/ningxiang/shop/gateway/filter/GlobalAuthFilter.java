package com.ningxiang.shop.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 网关全局过滤器：负责在鉴权通过后，将 Sa-Session 中的用户信息透传到下游微服务
 *
 * @author Ningxiang
 */
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果当前请求已登录，则提取用户信息传递给下游微服务
        if (StpUtil.isLogin()) {
            try {
                Object userInfoObj = StpUtil.getSession().get("userInfo");
                if (userInfoObj != null) {
                    // 序列化用户信息对象为 JSON
                    String userInfoJson = objectMapper.writeValueAsString(userInfoObj);
                    // 进行 URL 编码，防止 Header 乱码
                    String encodedUserInfo = URLEncoder.encode(userInfoJson, StandardCharsets.UTF_8.toString());

                    // 构建下游请求，并追加 x-user-info 请求头
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("x-user-info", encodedUserInfo)
                            .build();

                    return chain.filter(exchange.mutate().request(request).build());
                }
            } catch (Exception e) {
                // 降级处理，不阻塞主流程
                e.printStackTrace();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 注意：执行顺序必须排在 SaReactorFilter 校验通过之后
        return 100;
    }
}

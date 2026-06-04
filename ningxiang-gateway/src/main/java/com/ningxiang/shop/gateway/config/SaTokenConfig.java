package com.ningxiang.shop.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 网关全局过滤拦截器配置
 *
 * @author Ningxiang
 */
@Configuration
public class SaTokenConfig {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截所有路径
                .addInclude("/**")
                // 放行白名单和静态资源
                .addExclude(
                        "/favicon.ico",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                )
                // 放行微服务对外的匿名访问路径（Un-Authorized）
                .addExclude("/**/ua/**")
                // 放行特定的登录/注册及对外公共接口
                .addExclude(
                        "/login",
                        "/register",
                        "/auth/login",
                        "/auth/register",
                        "/leaf/api/**"
                )
                // 路由鉴权逻辑
                .setAuth(obj -> {
                    // 校验需要授权的移动端接口 (Authorized)
                    SaRouter.match("/**/a/**", r -> StpUtil.checkLogin());
                    // 校验后台管理端接口 (Admin)
                    SaRouter.match("/**/admin/**", r -> StpUtil.checkLogin());
                    // 校验商家端接口 (Multishop)
                    SaRouter.match("/**/multishop/**", r -> StpUtil.checkLogin());
                })
                // 认证失败时的异常处理
                .setError(e -> SaResult.error(e.getMessage()));
    }
}

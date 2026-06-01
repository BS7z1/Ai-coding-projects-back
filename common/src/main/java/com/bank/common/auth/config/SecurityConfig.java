package com.bank.common.auth.config;

import com.bank.common.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.PrintWriter;

/**
 * Spring Security 配置
 * <p>
 * 策略：Session 认证（前后端分离 JSON 模式）
 * - 登录/注销接口白名单放行
 * - 未认证返回 JSON 401，不做页面重定向
 * - 已有的所有业务接口（/api/**）未登录时返回 401，由前端路由守卫跳转 /login
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * BCrypt 密码编码器（供 AuthService 注入）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 核心安全过滤链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ---- 关闭 CSRF（前后端分离 Session 模式，前端不提交 CSRF token）----
            .csrf().disable()

            // ---- 接口权限规则 ----
            .authorizeRequests(auth -> auth
                // 白名单：登录、注销、应用信息接口，无需认证
                .antMatchers(
                    "/api/login.do",
                    "/api/logout",
                    "/api/appinfo"
                ).permitAll()
                // 其余所有 /api/** 接口需要登录
                .antMatchers("/api/**").authenticated()
                // 前端静态资源（npm run dev 代理时不经过 Spring，生产打包时放行）
                .anyRequest().permitAll()
            )

            // ---- 未认证处理：返回 JSON 401，不重定向 ----
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                    Result<?> result = Result.error(401, "未登录或登录已过期，请重新登录");
                    PrintWriter writer = response.getWriter();
                    writer.write(objectMapper.writeValueAsString(result));
                    writer.flush();
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                    Result<?> result = Result.error(403, "权限不足");
                    PrintWriter writer = response.getWriter();
                    writer.write(objectMapper.writeValueAsString(result));
                    writer.flush();
                })
            )

            // ---- 禁用 Spring Security 默认的 form login 和 basic auth ----
            // （我们自己实现了 /api/login.do，不使用 Spring Security 的登录页）
            .formLogin().disable()
            .httpBasic().disable()

            // ---- Session 管理 ----
            .sessionManagement(session -> session
                // 同一账号只能有一个活跃 Session（可选，如需允许多端登录则删除此行）
                .maximumSessions(10)
            );

        return http.build();
    }
}

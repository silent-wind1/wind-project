package com.yefeng.yefengaicode.config;

import lombok.extern.slf4j.Slf4j;
                                                                                                                                                                                                                                                                                                                                        import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 处理跨域和URL重定向问题
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
        
        log.info("✅ CORS跨域配置完成");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 禁用自动重定向，避免URL末尾自动添加斜杠
        // 这样可以防止 /api/static/1.jpg 被重定向到 /api/static/1.jpg/
        log.info("✅ 禁用自动重定向配置完成");
    }
}

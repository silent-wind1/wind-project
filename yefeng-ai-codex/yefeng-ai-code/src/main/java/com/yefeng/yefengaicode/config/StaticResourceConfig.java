package com.yefeng.yefengaicode.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * SpringBoot 3 静态资源配置
 * 专门处理文件上传后的静态资源访问
 * 解决URL自动添加斜杠的问题
 */
@Slf4j
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload.base-path:D:/uploads}")
    private String uploadBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("=== SpringBoot 3 静态资源配置 ===");
        log.info("上传文件根目录: {}", uploadBasePath);
        
        try {
            // 验证目录是否存在
            Path basePath = Paths.get(uploadBasePath);
            if (!basePath.toFile().exists()) {
                log.warn("警告: 上传目录不存在: {}", uploadBasePath);
                log.warn("请确保目录存在且有正确的读写权限");
            } else {
                log.info("✅ 上传目录存在: {}", uploadBasePath);
            }

            // 方式1: 映射到整个uploads目录，使用精确匹配避免斜杠问题
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + uploadBasePath + "/")
                    .setCachePeriod(3600)
                    .resourceChain(true)
                    .addResolver(new PathResourceResolver());
            log.info("✅ 配置uploads映射: /uploads/** -> {}", uploadBasePath);

        } catch (Exception e) {
            log.error("❌ 静态资源配置失败", e);
        }
        
        log.info("=== 静态资源配置完成 ===");
    }
}

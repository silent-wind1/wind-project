package com.yefeng.yefengaicode.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    
    /**
     * 文件保存根目录（绝对路径）
     */
    private String basePath = "D:/uploads";

    /**
     * 图片文件子目录
     */
    private String imagePath = "/images";

    /**
     * 其他文件子目录
     */
    private String otherPath = "/files";

    /**
     * 文件名前缀
     */
    String prefix = "yefeng-";
    
    /**
     * 文件访问域名（用于构建完整的访问URL）
     */
    private String accessDomain = "http://localhost:2411";
    
    /**
     * 允许的图片文件类型
     */
    private String[] allowedImageTypes = {"jpeg", "jpg", "svg", "png", "webp", "gif", "bmp"};
    
    /**
     * 最大文件大小（字节）
     */
    private long maxFileSize = 15 * 1024 * 1024; // 15MB
}

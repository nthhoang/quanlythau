package com.quanlythau.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String duongDanChuanHoa = chuanHoaDuongDan(uploadDir);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + duongDanChuanHoa + "/");
    }

    private String chuanHoaDuongDan(String dir) {
        return dir.endsWith("/") ? dir.substring(0, dir.length() - 1) : dir;
    }
}

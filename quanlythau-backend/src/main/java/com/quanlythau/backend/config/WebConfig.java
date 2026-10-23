package com.quanlythau.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String duongDanChuanHoa = Path.of(uploadDir).toAbsolutePath().normalize().toUri().toString();
        if (!duongDanChuanHoa.endsWith("/")) {
            duongDanChuanHoa += "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(duongDanChuanHoa);
    }
}

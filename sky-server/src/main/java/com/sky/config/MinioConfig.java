package com.sky.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {
	
	@Value("${minio.url}")
    private String url;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    
    @Bean
    public MinioClient getMinioClient() {
        log.info("注册minio:{}",url);
        return MinioClient.builder().endpoint(url)
				.credentials(accessKey, secretKey).build();
    }
    
}
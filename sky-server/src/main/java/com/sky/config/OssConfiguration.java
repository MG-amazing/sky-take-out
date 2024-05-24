package com.sky.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于配置ali oss配置对象
 */

@Configuration
@Slf4j
public class OssConfiguration {
//    @Bean
//    @ConditionalOnMissingBean
//    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
//        log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
//        return new AliOssUtil(aliOssProperties.getEndpoint(),
//                aliOssProperties.getAccessKeyId(),
//                aliOssProperties.getAccessKeySecret(),
//                aliOssProperties.getBucketName());
//    }
}

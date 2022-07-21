package com.halead.sso.config;

import com.aliyun.oss.OSSClient;
import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @title: AliyunConfig
 * @Author ppjjss
 * @Date: 2022/6/29 19:44
 * @Version 1.0
 */
@Configuration
@PropertySource("classpath:aliyunoss.properties")
@ConfigurationProperties(prefix = "aliyun")
@Data
public class AliyunConfig {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String urlPrefix;

    @Bean
    public OSSClient ossClient(){
        return new OSSClient(endpoint,accessKeyId,accessKeySecret);
    }
}

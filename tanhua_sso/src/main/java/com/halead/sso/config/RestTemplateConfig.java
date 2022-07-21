package com.halead.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * @title: RestTemplateConfig
 * @Author ppjjss
 * @Date: 2022/6/28 21:32
 * @Version 1.0
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return  restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
        return factory;
    }

}

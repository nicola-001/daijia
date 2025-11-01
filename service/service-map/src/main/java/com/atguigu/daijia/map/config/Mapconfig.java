package com.atguigu.daijia.map.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Mapconfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

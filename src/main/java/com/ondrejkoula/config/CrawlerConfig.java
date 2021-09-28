package com.ondrejkoula.config;

import com.ondrejkoula.crawler.CrawlerContext;
import com.ondrejkoula.crawler.UuidProvider;
import com.ondrejkoula.crawler.messages.CrawlerMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public abstract class CrawlerConfig {

    @Bean
    public UuidProvider uuidProvider() {
        return UUID::randomUUID;
    }

    @Bean
    public CrawlerContext crawlerContext(UuidProvider uuidProvider) {
        return new CrawlerContext(uuidProvider);
    }

    @Bean
    public abstract CrawlerMessageService messageService();
}

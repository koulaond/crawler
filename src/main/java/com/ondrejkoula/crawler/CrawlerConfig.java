package com.ondrejkoula.crawler;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.net.URL;
import java.util.Set;

@Getter
@Builder
public class CrawlerConfig {
    @Singular
    private Set<SupportedType> excludedTypes;
    private Set<URL> urlsToSkip;
    @Singular
    private Set<URL> initialUrls;
    private String userAgent;
    private int crawlDelayMillis;
}

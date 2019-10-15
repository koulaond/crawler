package com.ondrejkoula.crawler;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.net.URL;
import java.util.Set;

@Getter
@Builder
public class CrawlerConfig {

    private URL initUrl;

    @Singular
    private Set<SupportedType> excludedTypes;

    private String userAgent;
}

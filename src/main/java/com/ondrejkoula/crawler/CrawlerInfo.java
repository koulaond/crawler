package com.ondrejkoula.crawler;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CrawlerInfo {
    private UUID uuid;
    private String host;
    private CrawlerState state;
}

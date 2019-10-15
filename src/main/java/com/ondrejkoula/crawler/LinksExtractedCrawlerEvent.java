package com.ondrejkoula.crawler;

import lombok.Getter;

import java.net.URL;
import java.util.Set;
import java.util.UUID;

@Getter
public final class LinksExtractedCrawlerEvent  extends CrawlerEvent{

    private final URL source;

    private final Set<URL> links;

    public LinksExtractedCrawlerEvent(UUID crawlerUuid, URL source, Set<URL> links) {
        super(crawlerUuid);
        this.source = source;
        this.links = links;
    }
}

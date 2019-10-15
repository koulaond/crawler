package com.ondrejkoula.crawler;

import lombok.Getter;

import java.net.URL;
import java.util.UUID;

@Getter
public final class DataAcquiredCrawlerEvent extends CrawlerEvent {

    private final URL location;

    private final String documentTitle;

    private final String documentHtml;

    public DataAcquiredCrawlerEvent(UUID crawlerUuid, URL location, String documentTitle, String documentHtml) {
        super(crawlerUuid);
        this.location = location;
        this.documentTitle = documentTitle;
        this.documentHtml = documentHtml;
    }
}

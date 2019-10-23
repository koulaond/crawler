package com.ondrejkoula.crawler;

import lombok.Getter;

import java.net.URL;
import java.util.Set;
import java.util.UUID;

@Getter
public final class PageDataAcquiredCrawlerEvent extends CrawlerEvent {

    private final URL location;
    private final String documentTitle;
    private final String documentHtml;
    private final Set<URL> outcomeUrlsOutOfDomain;
    private final Set<URL> outcomeUrlsOnDomain;

    PageDataAcquiredCrawlerEvent(UUID crawlerUuid,
                                 URL location,
                                 String documentTitle,
                                 String documentHtml,
                                 Set<URL> outcomeUrlsOutOfDomain,
                                 Set<URL> outcomeUrlsOnDomain) {
        super(crawlerUuid);
        this.location = location;
        this.documentTitle = documentTitle;
        this.documentHtml = documentHtml;
        this.outcomeUrlsOutOfDomain = outcomeUrlsOutOfDomain;
        this.outcomeUrlsOnDomain = outcomeUrlsOnDomain;
    }
}

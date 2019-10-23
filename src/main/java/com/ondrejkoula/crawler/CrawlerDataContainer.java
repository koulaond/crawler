package com.ondrejkoula.crawler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class CrawlerDataContainer {

    private Queue<CrawlerURL> urlsToCrawl;
    private Set<CrawlerURL> crawledUrls;
    private Set<CrawlerURL> failedUrls;

    public CrawlerDataContainer() {
        this.urlsToCrawl = new LinkedList<>();
        this.crawledUrls = new HashSet<>();
        this.failedUrls = new HashSet<>();
    }

    CrawlerDataContainer(Set<CrawlerURL> urlsToSkip, Set<CrawlerURL> urlsToCrawl) {
        this.urlsToCrawl = urlsToCrawl == null ? new LinkedList<>() : new LinkedList<>(urlsToCrawl);
        this.crawledUrls = urlsToSkip == null ? new HashSet<>() : urlsToSkip;
        this.failedUrls = new HashSet<>();
    }

    boolean addToQueueIfNotProcessed(CrawlerURL url) {
        return !isDone(url) && !urlsToCrawl.contains(url) && urlsToCrawl.add(url);
    }

    CrawlerURL nextUrl() {
        return urlsToCrawl.poll();
    }

    boolean markAsCrawled(CrawlerURL url) {
        return crawledUrls.contains(url) || crawledUrls.add(url);
    }

    boolean markAsFailed(CrawlerURL url) {
        return failedUrls.contains(url) || failedUrls.add(url);
    }

    boolean isDone(CrawlerURL url) {
        return crawledUrls.contains(url) || failedUrls.contains(url);
    }

    public void clearData() {
        urlsToCrawl.clear();
        crawledUrls.clear();
        failedUrls.clear();
    }
}

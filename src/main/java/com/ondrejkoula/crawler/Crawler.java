package com.ondrejkoula.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static com.ondrejkoula.crawler.CrawlerState.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.jsoup.Jsoup.connect;

public class Crawler implements Runnable {

    private final UUID uuid;
    private final CrawlerConfig config;
    private final MessageService messageService;
    private final CrawlerDataContainer dataContainer;
    private final LinksFilter linksFilter;
    private final CrawlerEventHandler eventHandler;
    private final ReentrantLock lock;

    private CrawlerState currentState;

    Crawler(UUID uuid, CrawlerConfig crawlerConfig, MessageService messageService, CrawlerEventHandler eventHandler) {
        this.uuid = uuid;
        this.config = crawlerConfig;
        this.messageService = messageService;
        this.eventHandler = eventHandler;
        this.dataContainer = new CrawlerDataContainer();
        this.linksFilter = new LinksFilter();
        this.lock = new ReentrantLock();
        changeState(CrawlerState.NEW);
    }

    private void startCrawling() {
        if (!NEW.equals(currentState)) {
            messageService.crawlerWarning(uuid, "Crawler already started.");
        }
        changeState(RUNNING);
        CrawlerURL initUrl = new CrawlerURL(config.getInitUrl());
        if (!proceedUrl(initUrl)) {
            if (!STOPPED.equals(currentState)) {
                changeState(FAILED);
            }
            return;
        }
        CrawlerURL nextUrl;
        while ((nextUrl = dataContainer.nextUrl()) != null) {
            if (!STOPPED.equals(currentState)) {
                proceedUrl(nextUrl);
            }
        }
        changeState(FINISHED);
    }

    public void pause() {
        if (!STOPPED.equals(currentState)) {
            lock.lock();
            changeState(CrawlerState.PAUSED);
        }
    }

    public void resume() {
        if (lock.isLocked() && !STOPPED.equals(currentState)) {
            changeState(RUNNING);
            lock.unlock();
        }
    }

    public void stop() {
        if (RUNNING.equals(currentState) || PAUSED.equals(currentState)) {
            changeState(STOPPED);
        }
    }

    private boolean proceedUrl(CrawlerURL url) {
        // 1. critical section - download source
        Document htmlDocument;
        try {
            lock.lock();
            if (STOPPED.equals(currentState)) {
                return false;
            }
            htmlDocument = connect(url.getUrl().toString())
                    .userAgent(config.getUserAgent())
                    .get();
        } catch (IOException e) {
            dataContainer.markAsFailed(url);
            messageService.crawlerError(uuid, format("Cannot get HTML from %s", url.toString()), e);
            return false;
        } finally {
            lock.unlock();
        }
        // 2. critical section - process all extracted links
        Set<String> filteredLinks = processLinks(url, htmlDocument);
        // 3. critical section - add all links to crawl queue
        queueLinks(filteredLinks);
        return true;
    }

    private Set<String> processLinks(CrawlerURL url, Document htmlDocument) {
        try {
            lock.lock();
            proceedHtml(htmlDocument, url);
            dataContainer.markAsCrawled(url);
            Elements outcomeLinks = htmlDocument.select("a[href]");
            Set<String> filteredLinks = linksFilter.filterLinks(outcomeLinks, config.getExcludedTypes());
            notifyLinksAcquired(filteredLinks, url);
            return filteredLinks;
        } finally {
            lock.unlock();
        }
    }

    private void queueLinks(Set<String> filteredLinks) {
        for (String link : filteredLinks) {
            try {
                lock.lock();
                CrawlerURL outcomeLink = new CrawlerURL(new URL(link));
                if (isOnDomain(outcomeLink)) {
                    dataContainer.addToQueueIfNotProcessed(outcomeLink);
                }
            } catch (MalformedURLException e) {
                messageService.crawlerError(uuid, format("Invalid link: %s. Skipping...", link, e));
            } finally {
                lock.unlock();
            }
        }
    }

    private void notifyLinksAcquired(Set<String> extractedLinks, CrawlerURL url) {
        Set<URL> urls = extractedLinks.stream()
                .map(link -> {
                    try {
                        return new URL(link);
                    } catch (MalformedURLException e) {
                        messageService.crawlerError(uuid, format("Link process failed. Invalid URL: %s", link), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(toSet());
        LinksExtractedCrawlerEvent event = new LinksExtractedCrawlerEvent(uuid, url.getUrl(), urls);
        eventHandler.notify(event);
    }

    private void proceedHtml(Document htmlDocument, CrawlerURL location) {
        DataAcquiredCrawlerEvent event
                = new DataAcquiredCrawlerEvent(uuid, location.getUrl(), htmlDocument.title(), htmlDocument.outerHtml());
        eventHandler.notify(event);
    }

    private void changeState(CrawlerState newState) {
        CrawlerState oldState = currentState;
        currentState = newState;
        eventHandler.notify(new StateChangedCrawlerEvent(uuid, oldState, newState));
    }

    private boolean isOnDomain(CrawlerURL url) {
        return Objects.equals(url.getUrl().getHost(), config.getInitUrl().getHost());
    }

    @Override
    public void run() {
        startCrawling();
    }
}

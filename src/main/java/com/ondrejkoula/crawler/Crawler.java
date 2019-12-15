package com.ondrejkoula.crawler;

import com.ondrejkoula.crawler.messages.CrawlerMessageService;
import lombok.Getter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.ondrejkoula.crawler.CrawlerState.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.jsoup.Jsoup.connect;

public class Crawler implements Runnable {
    @Getter
    private final UUID uuid;
    @Getter
    private final CrawlerConfig config;
    private final CrawlerMessageService messageService;
    private final CrawlerDataContainer dataContainer;
    private final LinksFilter linksFilter;
    private final CrawlerEventHandler eventHandler;
    private final ReentrantLock lock;

    @Getter
    private CrawlerState currentState;
    @Getter
    private String host;

    Crawler(UUID uuid, CrawlerConfig crawlerConfig, CrawlerMessageService messageService, CrawlerEventHandler eventHandler) {
        Set<URL> initialUrls = crawlerConfig.getInitialUrls() == null ? new HashSet<>() : crawlerConfig.getInitialUrls();
        if (initialUrls.isEmpty()) {
            throw new IllegalStateException("No initial URL specified.");
        }
        validateUrlsHosts(initialUrls);
        this.uuid = uuid;
        this.config = crawlerConfig;
        this.messageService = messageService;
        this.eventHandler = eventHandler;
        this.host = initialUrls.iterator().next().getHost();
        Set<URL> urlsToSkip = crawlerConfig.getUrlsToSkip() == null ? new HashSet<>() : crawlerConfig.getUrlsToSkip();

        this.dataContainer = new CrawlerDataContainer(
                urlsToSkip
                        .stream()
                        .map(CrawlerURL::new)
                        .collect(Collectors.toSet()),
                initialUrls
                        .stream()
                        .map(CrawlerURL::new)
                        .collect(toSet()));
        this.linksFilter = new LinksFilter();
        this.lock = new ReentrantLock();
        changeState(CrawlerState.NEW);
    }

    private void validateUrlsHosts(Set<URL> initialUrls) {
        if (initialUrls.stream().map(URL::getHost).collect(toSet()).size() > 1) {
            throw new IllegalStateException("Distinct hosts in initial URLs.");
        }
    }

    private void startCrawling() {
        if (!NEW.equals(currentState)) {
            messageService.crawlerWarning(uuid, "Crawler already started.");
        }
        changeState(RUNNING);

        CrawlerURL initUrl = dataContainer.nextUrl();
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
        if (!STOPPED.equals(currentState)) {
            changeState(FINISHED);
        }
    }

    void pause() {
        if (!STOPPED.equals(currentState)) {
            lock.lock();
            changeState(CrawlerState.PAUSED);
        }
    }

    void resume() {
        if (lock.isLocked() && !STOPPED.equals(currentState)) {
            changeState(RUNNING);
            lock.unlock();
        }
    }

    void stop() {
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
            dataContainer.markAsCrawled(url);
            Elements outcomeLinks = htmlDocument.select("a[href]");
            Set<String> filteredLinks = linksFilter.filterLinks(outcomeLinks, config.getExcludedTypes());
            notifyDataAcquired(htmlDocument, filteredLinks, url);
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
                messageService.crawlerError(uuid, format("Invalid link: %s. Skipping...", link));
            } finally {
                lock.unlock();
            }
        }
    }

    private void notifyDataAcquired(Document document, Set<String> extractedLinks, CrawlerURL crawlerURL) {
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
        Set<URL> urlsOutOfDomain = urls.stream().filter(url -> !url.getHost().equals(this.host)).collect(toSet());
        Set<URL> urlsOnDomain = urls.stream().filter(url -> url.getHost().equals(this.host)).collect(toSet());
        PageDataAcquiredCrawlerEvent event = new PageDataAcquiredCrawlerEvent(uuid, crawlerURL.getUrl(), document.title(), document.outerHtml(), urlsOutOfDomain, urlsOnDomain);
        eventHandler.notify(event);
    }


    private void changeState(CrawlerState newState) {
        CrawlerState oldState = currentState;
        currentState = newState;
        eventHandler.notify(new StateChangedCrawlerEvent(uuid, oldState, newState));
    }

    private boolean isOnDomain(CrawlerURL url) {
        return Objects.equals(url.getUrl().getHost(), this.host);
    }

    @Override
    public void run() {
        startCrawling();
    }
}

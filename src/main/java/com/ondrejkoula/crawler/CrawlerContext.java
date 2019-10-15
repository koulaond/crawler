package com.ondrejkoula.crawler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CrawlerContext {
    private static CrawlerContext INSTANCE;

    private final Map<UUID, Crawler> registeredCrawlers;

    private final CrawlerEventHandler eventHandler;

    private ErrorService errorService;

    private UuidProvider uuidProvider;

    private CrawlerContext() {
        this.registeredCrawlers = new ConcurrentHashMap<>();
        this.eventHandler = new CrawlerEventHandler();
    }

    public void subscribeDataAcquired(UUID crawlerUuid, Consumer<DataAcquiredCrawlerEvent>... consumers) {
        if (registeredCrawlers.get(crawlerUuid) != null) {
            for (Consumer<DataAcquiredCrawlerEvent> consumer : consumers)
                this.eventHandler.subscribeDataAcquired(crawlerUuid, consumer);
        }
    }

    public void subscribeLinksExtracted(UUID crawlerUuid, Consumer<LinksExtractedCrawlerEvent>... consumers) {
        if (registeredCrawlers.get(crawlerUuid) != null) {
            for (Consumer<LinksExtractedCrawlerEvent> consumer : consumers)
                this.eventHandler.subscribeLinksExtracted(crawlerUuid, consumer);
        }
    }

    public void subscribeStateChanged(UUID crawlerUuid, Consumer<StateChangedCrawlerEvent>... consumers) {
        if (registeredCrawlers.get(crawlerUuid) != null) {
            for (Consumer<StateChangedCrawlerEvent> consumer : consumers)
                this.eventHandler.subscribeStateChanged(crawlerUuid, consumer);
        }
    }

    public UUID registerNewCrawler(CrawlerConfig config) {
        UUID uuid = uuidProvider.newUuid();
        Crawler crawler = new Crawler(uuid, config, errorService, eventHandler);
        registeredCrawlers.put(uuid, crawler);
        return uuid;
    }

    // TODO replace by Spring annotations
    public static CrawlerContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrawlerContext();
        }
        return INSTANCE;
    }
}

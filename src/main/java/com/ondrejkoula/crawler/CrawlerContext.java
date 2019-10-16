package com.ondrejkoula.crawler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CrawlerContext {
    private static CrawlerContext INSTANCE;

    private final Map<UUID, Crawler> registeredCrawlers;

    private final CrawlerEventHandler eventHandler;

    private MessageService messageService;

    private UuidProvider uuidProvider;

    private CrawlerContext(UuidProvider uuidProvider, MessageService messageService) {
        this.registeredCrawlers = new ConcurrentHashMap<>();
        this.eventHandler = new CrawlerEventHandler();
        this.uuidProvider = uuidProvider;
        this.messageService = messageService;
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
        Crawler crawler = new Crawler(uuid, config, messageService, eventHandler);
        registeredCrawlers.put(uuid, crawler);
        return uuid;
    }

    public void startCrawler(UUID crawlerUuid) {
        doActionWithrawler(crawlerUuid, crawler -> {
            new Thread(crawler).start();
        });
    }

    public void pauseCrawler(UUID crawlerUuid) {
        doActionWithrawler(crawlerUuid, crawler -> crawler.pause());
    }

    public void resumeCrawler(UUID crawlerUuid) {
        doActionWithrawler(crawlerUuid, crawler -> crawler.resume());
    }

    public void stopCrawler(UUID crawlerUuid) {
        doActionWithrawler(crawlerUuid, crawler -> crawler.stop());
    }

    private void doActionWithrawler(UUID crawlerUuid, Consumer<Crawler> crawlerConsumer) {
        Crawler crawler = registeredCrawlers.get(crawlerUuid);
        if (crawler != null) {
            crawlerConsumer.accept(crawler);
        }
    }

    // TODO replace by Spring annotations
    public static CrawlerContext getInstance() {
        if (INSTANCE == null) {
            //INSTANCE = new CrawlerContext();
        }
        return INSTANCE;
    }
}

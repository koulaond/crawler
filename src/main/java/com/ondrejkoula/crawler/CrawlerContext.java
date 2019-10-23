package com.ondrejkoula.crawler;

import com.ondrejkoula.crawler.messages.MessageService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CrawlerContext {

    private final Map<UUID, Crawler> registeredCrawlers;
    private final CrawlerEventHandler eventHandler;
    private final ExecutorService executorService;

    private MessageService messageService;
    private UuidProvider uuidProvider;

    public CrawlerContext(UuidProvider uuidProvider, MessageService messageService) {
        this.uuidProvider = uuidProvider;
        this.messageService = messageService;
        this.registeredCrawlers = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
        this.eventHandler = new CrawlerEventHandler(executorService);
    }

    @SafeVarargs
    public final void subscribePageDataAcquired(UUID crawlerUuid, Consumer<PageDataAcquiredCrawlerEvent>... consumers) {
        if (registeredCrawlers.get(crawlerUuid) != null) {
            for (Consumer<PageDataAcquiredCrawlerEvent> consumer : consumers)
                this.eventHandler.subscribePageDataAcquired(crawlerUuid, consumer);
        }
    }

    @SafeVarargs
    public final void subscribeStateChanged(UUID crawlerUuid, Consumer<StateChangedCrawlerEvent>... consumers) {
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
        doActionWithCrawler(crawlerUuid, executorService::execute);
    }

    public void pauseCrawler(UUID crawlerUuid) {
        doActionWithCrawler(crawlerUuid, Crawler::pause);
    }

    public void resumeCrawler(UUID crawlerUuid) {
        doActionWithCrawler(crawlerUuid, Crawler::resume);
    }

    public void stopCrawler(UUID crawlerUuid) {
        doActionWithCrawler(crawlerUuid, Crawler::stop);
    }

    private void doActionWithCrawler(UUID crawlerUuid, Consumer<Crawler> crawlerConsumer) {
        Crawler crawler = registeredCrawlers.get(crawlerUuid);
        if (crawler != null) {
            crawlerConsumer.accept(crawler);
        }
    }
}

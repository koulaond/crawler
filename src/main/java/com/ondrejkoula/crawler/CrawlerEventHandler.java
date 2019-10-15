package com.ondrejkoula.crawler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public final class CrawlerEventHandler {

    private final Map<UUID, Set<Consumer<DataAcquiredCrawlerEvent>>> dataAcquiredEvents;
    private final Map<UUID, Set<Consumer<LinksExtractedCrawlerEvent>>> linksExtractedEvents;
    private final Map<UUID, Set<Consumer<StateChangedCrawlerEvent>>> stateChangedEvents;

    private final ReentrantLock lock;

    public CrawlerEventHandler() {
        this.dataAcquiredEvents = new ConcurrentHashMap<>();
        this.linksExtractedEvents = new ConcurrentHashMap<>();
        this.stateChangedEvents = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    public <E extends CrawlerEvent> void notify(E event) {
        UUID crawlerUuid = event.getCrawlerUuid();
        lock.lock();
        try {
            if (event instanceof DataAcquiredCrawlerEvent) {
                ofNullable(dataAcquiredEvents.get(crawlerUuid))
                        .ifPresent(consumers -> consumers.forEach(consumer -> consumer.accept((DataAcquiredCrawlerEvent) event)));
            } else if (event instanceof LinksExtractedCrawlerEvent) {
                ofNullable(linksExtractedEvents.get(crawlerUuid))
                        .ifPresent(consumers -> consumers.forEach(consumer -> consumer.accept((LinksExtractedCrawlerEvent) event)));
            } else if (event instanceof StateChangedCrawlerEvent) {
                ofNullable(stateChangedEvents.get(crawlerUuid))
                        .ifPresent(consumers -> consumers.forEach(consumer -> consumer.accept((StateChangedCrawlerEvent) event)));
            } else
                throw new IllegalArgumentException(String.format("Unsupported event type: %s", event.getClass().getName()));
        } finally {
            lock.unlock();
        }

    }

    void subscribeDataAcquired(UUID crawlerUuid, Consumer<DataAcquiredCrawlerEvent> consumer) {
        dataAcquiredEvents.computeIfAbsent(crawlerUuid, o -> new HashSet<>()).add(consumer);
    }

    void subscribeLinksExtracted(UUID crawlerUuid, Consumer<LinksExtractedCrawlerEvent> consumer) {
        linksExtractedEvents.computeIfAbsent(crawlerUuid, o -> new HashSet<>()).add(consumer);
    }

    void subscribeStateChanged(UUID crawlerUuid, Consumer<StateChangedCrawlerEvent> consumer) {
        stateChangedEvents.computeIfAbsent(crawlerUuid, o -> new HashSet<>()).add(consumer);
    }
}

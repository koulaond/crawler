package com.ondrejkoula.crawler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

final class CrawlerEventHandler {

    private final Map<UUID, Set<Consumer<PageDataAcquiredCrawlerEvent>>> dataAcquiredConsumers;
    private final Map<UUID, Set<Consumer<StateChangedCrawlerEvent>>> stateChangedConsumers;

    private final ReentrantLock lock;
    private final ExecutorService executorService;

    CrawlerEventHandler(ExecutorService executorService) {
        this.executorService = executorService;
        this.dataAcquiredConsumers = new ConcurrentHashMap<>();
        this.stateChangedConsumers = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    <E extends CrawlerEvent> void notify(E event) {
        UUID crawlerUuid = event.getCrawlerUuid();
        lock.lock();
        try {
            if (event instanceof PageDataAcquiredCrawlerEvent) {
                ofNullable(dataAcquiredConsumers.get(crawlerUuid))
                        .ifPresent(consumers -> consumers.forEach(consumer -> {
                            executorService.execute(() -> consumer.accept((PageDataAcquiredCrawlerEvent) event));
                        }));
            } else if (event instanceof StateChangedCrawlerEvent) {
                ofNullable(stateChangedConsumers.get(crawlerUuid))
                        .ifPresent(consumers -> consumers.forEach(consumer -> {
                            executorService.execute(() -> consumer.accept((StateChangedCrawlerEvent) event));
                        }));
            } else
                throw new IllegalArgumentException(String.format("Unsupported event type: %s", event.getClass().getName()));
        } finally {
            lock.unlock();
        }

    }

    void subscribePageDataAcquired(UUID crawlerUuid, Consumer<PageDataAcquiredCrawlerEvent> consumer) {
        dataAcquiredConsumers.computeIfAbsent(crawlerUuid, o -> new HashSet<>()).add(consumer);
    }

    void subscribeStateChanged(UUID crawlerUuid, Consumer<StateChangedCrawlerEvent> consumer) {
        stateChangedConsumers.computeIfAbsent(crawlerUuid, o -> new HashSet<>()).add(consumer);
    }
}

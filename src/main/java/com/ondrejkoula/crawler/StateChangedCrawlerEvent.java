package com.ondrejkoula.crawler;

import lombok.Getter;

import java.util.UUID;

@Getter
public final class StateChangedCrawlerEvent extends CrawlerEvent {

    private final CrawlerState oldState;

    private final CrawlerState newState;

    public StateChangedCrawlerEvent(UUID crawlerUuid, CrawlerState oldState, CrawlerState newState) {
        super(crawlerUuid);
        this.oldState = oldState;
        this.newState = newState;
    }
}

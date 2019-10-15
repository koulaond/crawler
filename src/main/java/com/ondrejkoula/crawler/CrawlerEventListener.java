package com.ondrejkoula.crawler;

@FunctionalInterface
public interface CrawlerEventListener {

    void update(StateChangedCrawlerEvent event);
}

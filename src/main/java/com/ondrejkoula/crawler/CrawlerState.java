package com.ondrejkoula.crawler;

public enum CrawlerState {
    NEW,
    PENDING,
    RUNNING,
    PAUSED,
    FINISHED,
    STOPPED,
    FAILED
}

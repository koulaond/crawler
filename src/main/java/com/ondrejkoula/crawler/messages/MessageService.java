package com.ondrejkoula.crawler.messages;

import java.util.UUID;

public interface MessageService {
    void crawlerError(UUID crawlerUuid, String message);

    void crawlerError(UUID crawlerUuid, String message, Throwable throwable);

    void crawlerWarning(UUID crawlerUuid, String message);
}

package com.ondrejkoula.crawler.messages;

import java.util.UUID;

//@Slf4j
public class LoggerMessageService implements MessageService{
    @Override
    public void crawlerError(UUID crawlerUuid, String message) {
        // TODO log
    }

    @Override
    public void crawlerError(UUID crawlerUuid, String message, Throwable throwable) {
        // TODO log
    }

    @Override
    public void crawlerWarning(UUID crawlerUuid, String message) {
        // TODO log
    }
}

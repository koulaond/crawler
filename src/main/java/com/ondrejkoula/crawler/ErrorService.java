package com.ondrejkoula.crawler;

import java.util.UUID;

public class ErrorService {
    public void crawlerError(UUID crawlerUuid, String message){
        // TODO
    }

    public void crawlerError(UUID crawlerUuid, String message, Throwable throwable){
        System.err.println(throwable);
    }
}

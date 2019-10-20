package com.ondrejkoula;

import com.ondrejkoula.crawler.CrawlerConfig;
import com.ondrejkoula.crawler.CrawlerContext;
import com.ondrejkoula.crawler.messages.LoggerMessageService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static com.ondrejkoula.crawler.SupportedType.*;
import static java.lang.String.format;

public class Test {
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        CrawlerContext crawlerContext = new CrawlerContext(() -> UUID.randomUUID(), new LoggerMessageService());
        UUID crawlerUuid = crawlerContext.registerNewCrawler(CrawlerConfig.builder()
                .initialUrl(new URL("https://www.memsource.com/"))
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")
                .excludedTypes(binaries())
                .excludedTypes(audioTypes())
                .excludedTypes(imageTypes())
                .excludedTypes(archives())
                .build());

        crawlerContext.subscribePageDataAcquired(
                crawlerUuid,
                event -> System.out.println(
                        format("Crawler with ID:%s extracted %s, title: %s",
                                event.getCrawlerUuid(),
                                event.getLocation(),
                                event.getDocumentTitle())));

        crawlerContext.subscribeStateChanged(
                crawlerUuid,
                event -> System.out.println(
                        format("Crawler with ID:%s changed state %s -> %s",
                                event.getCrawlerUuid(),
                                event.getOldState(),
                                event.getNewState())));

        crawlerContext.startCrawler(crawlerUuid);
        Thread.sleep(1000);
        crawlerContext.pauseCrawler(crawlerUuid);
        Thread.sleep(1000);
        crawlerContext.resumeCrawler(crawlerUuid);
        Thread.sleep(1000);
        crawlerContext.stopCrawler(crawlerUuid);
        Thread.sleep(1000);
    }
}

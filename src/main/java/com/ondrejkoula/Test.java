package com.ondrejkoula;

import com.ondrejkoula.crawler.CrawlerConfig;
import com.ondrejkoula.crawler.CrawlerContext;
import com.ondrejkoula.crawler.CrawlerInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static com.ondrejkoula.crawler.SupportedType.*;

public class Test {
    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        // set cert props for windows (only for runtime)
        System.setProperty("javax.net.ssl.trustStore", "NUL");
        System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");

        CrawlerContext crawlerContext = new CrawlerContext(UUID::randomUUID);
        CrawlerInfo crawlerInfo = crawlerContext.registerNewCrawler(CrawlerConfig.builder()
                .initialUrl(new URL("https://www.memsource.com/"))
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36")
                .excludedTypes(binaries())
                .excludedTypes(audioTypes())
                .excludedTypes(imageTypes())
                .excludedTypes(archives())
                .build());
        UUID crawlerUuid = crawlerInfo.getUuid();
        crawlerContext.subscribePageDataAcquired(
                crawlerUuid,
                event -> System.out.printf("Crawler with ID:%s extracted %s, title: %s%n",
                        event.getCrawlerUuid(),
                        event.getLocation(),
                        event.getDocumentTitle()));

        crawlerContext.subscribeStateChanged(
                crawlerUuid,
                event -> System.out.printf("Crawler with ID:%s changed state %s -> %s%n",
                        event.getCrawlerUuid(),
                        event.getOldState(),
                        event.getNewState()));

        crawlerContext.startCrawler(crawlerUuid);
//        Thread.sleep(1000);
//        crawlerContext.pauseCrawler(crawlerUuid);
//        Thread.sleep(1000);
//        crawlerContext.resumeCrawler(crawlerUuid);
//        Thread.sleep(1000);
//        crawlerContext.stopCrawler(crawlerUuid);
        Thread.sleep(1000);
    }
}

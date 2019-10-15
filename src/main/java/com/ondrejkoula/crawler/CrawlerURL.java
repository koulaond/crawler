package com.ondrejkoula.crawler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URL;

@Getter
@RequiredArgsConstructor
public class CrawlerURL {

    private final URL url;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CrawlerURL)) return false;
        CrawlerURL that = (CrawlerURL) obj;

        return (this.url == null && that.getUrl() == null)
                || (this.url != null && that.getUrl() != null
                && new EqualsBuilder()
                .append(this.url.getProtocol(), that.getUrl().getProtocol())
                .append(this.url.getHost(), that.getUrl().getHost())
                .append(this.url.getPath(), that.getUrl().getPath())
                .isEquals()
        );
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.url.getProtocol())
                .append(this.url.getHost())
                .append(this.url.getPath())
                .build();
    }
}

package com.ondrejkoula.crawler;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.select.Elements;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public class LinksFilter {

    public Set<String> filterLinks(Elements elements, Set<SupportedType> excludedTypes) {
        Set<String> excludedTypesFlatten = excludedTypes.stream()
                .flatMap(supportedType -> newHashSet(supportedType.getExtensions()).stream())
                .collect(toSet());

        return elements.stream()
                .map(link -> link.attr("abs:href"))
                .filter(link -> {
                    for (String excludedType : excludedTypesFlatten) {
                        if (StringUtils.endsWith(link, excludedType)){
                            return false;
                        }
                    }
                    return true;
                })
        .collect(toSet());
    }
}

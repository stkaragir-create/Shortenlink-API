package com.example.spring_boot_url_shortlink.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class UrlService {

    private final Map<String, String> shortToLong = new ConcurrentHashMap<>();
    private final Map<String, String> longToShort = new ConcurrentHashMap<>();
    private final Map<String, Integer> domainCount = new ConcurrentHashMap<>();

    private final AtomicLong counter = new AtomicLong(1);

    public String shortenUrl(String longUrl) {
        return longToShort.computeIfAbsent(longUrl, url -> {
            String shortCode = encode(counter.getAndIncrement());
            shortToLong.put(shortCode, url);
            updateDomainMetrics(url);
            return shortCode;
        });
    }

    public String getOriginalUrl(String shortCode) {
        return shortToLong.get(shortCode);
    }

    private void updateDomainMetrics(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if (domain != null) {
                domainCount.merge(domain, 1, Integer::sum);
            }
        } catch (Exception ignored) {
        }
    }

    public Map<String, Integer> topDomains() {
        return domainCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private String encode(long num) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(chars.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}
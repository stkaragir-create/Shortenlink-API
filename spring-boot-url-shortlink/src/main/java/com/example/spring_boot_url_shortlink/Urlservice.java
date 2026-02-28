package com.example.spring_boot_url_shortlink;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@Service
public class Urlservice {

    private Map<String, String> shortToLong = new ConcurrentHashMap<>();
    private Map<String, String> longToShort = new ConcurrentHashMap<>();
    private Map<String, Integer> domainCount = new ConcurrentHashMap<>();

    private AtomicLong counter = new AtomicLong(1);

    public String shortenUrl(String longUrl) {

        if (longToShort.containsKey(longUrl)) {
            return longToShort.get(longUrl);
        }

        String shortCode = encode(counter.getAndIncrement());

        shortToLong.put(shortCode, longUrl);
        longToShort.put(longUrl, shortCode);

        updateDomainMetrics(longUrl);

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        return shortToLong.get(shortCode);
    }

    private void updateDomainMetrics(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            domainCount.put(domain, domainCount.getOrDefault(domain, 0) + 1);
        } catch (Exception e) {
            throw new RuntimeException("Invalid URL");
        }
    }

    public Map<String, Integer> topDomains() {
        return domainCount.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
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
            sb.append(chars.charAt((int)(num % 62)));
            num /= 62;
        }

        return sb.reverse().toString();
    }
}
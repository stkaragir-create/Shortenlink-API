package com.example.spring_boot_url_shortlink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class UrlController {

    @Autowired
    private Urlservice service;

    @PostMapping("/shorten")
    public Map<String, String> shorten(@RequestBody Map<String, String> request) {

        String longUrl = request.get("url");
        String shortCode = service.shortenUrl(longUrl);

        return Map.of("shortUrl", "http://localhost:8080/" + shortCode);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        String originalUrl = service.getOriginalUrl(shortCode);

        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/metrics/top-domains")
    public Map<String, Integer> metrics() {
        return service.topDomains();
    }
}
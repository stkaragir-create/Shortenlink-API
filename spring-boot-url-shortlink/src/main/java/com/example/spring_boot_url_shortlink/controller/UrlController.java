package com.example.spring_boot_url_shortlink.controller;

import com.example.spring_boot_url_shortlink.UrlRequest;
import com.example.spring_boot_url_shortlink.dto.UrlResponse;
import com.example.spring_boot_url_shortlink.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(@RequestBody UrlRequest request) {
        if (request.getUrl() == null || request.getUrl().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        String shortCode = service.shortenUrl(request.getUrl());
        return ResponseEntity.ok(new UrlResponse("http://localhost:8080/" + shortCode));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = service.getOriginalUrl(shortCode);
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }

    @GetMapping("/metrics/top-domains")
    public ResponseEntity<Map<String, Integer>> metrics() {
        return ResponseEntity.ok(service.topDomains());
    }
}
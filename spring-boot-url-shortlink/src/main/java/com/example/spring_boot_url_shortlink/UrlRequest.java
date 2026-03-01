package com.example.spring_boot_url_shortlink;

public class UrlRequest {
    private String url;

    public UrlRequest() {}  // default constructor for Spring

    public UrlRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
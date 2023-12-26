package com.victor.sparkcrawler.domain;

public class CrawlerHttpResponse {

    private final int statusCode;
    private final String content;

    public CrawlerHttpResponse(int statusCode, String content) {
        this.statusCode = statusCode;
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContent() {
        return content;
    }
}
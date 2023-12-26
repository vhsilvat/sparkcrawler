package com.victor.sparkcrawler.domain;

import java.util.Queue;

public class SearchResult {

    private String id;
    private String status;
    private Queue<String> urls;

    public SearchResult(String id, String status, Queue<String> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Queue<String> getUrls() {
        return urls;
    }

    public void setUrls(Queue<String> urls) {
        this.urls = urls;
    }
}

package com.victor.sparkcrawler.service;

import com.victor.sparkcrawler.domain.CrawlerHttpResponse;

import java.io.IOException;

public interface HttpService {
    CrawlerHttpResponse getContent(String url) throws IOException;
}

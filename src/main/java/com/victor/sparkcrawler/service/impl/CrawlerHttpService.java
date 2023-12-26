package com.victor.sparkcrawler.service.impl;

import com.victor.sparkcrawler.domain.CrawlerHttpResponse;
import com.victor.sparkcrawler.service.HttpService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CrawlerHttpService implements HttpService {

    @Override
    public CrawlerHttpResponse getContent(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                return new CrawlerHttpResponse(connection.getResponseCode(), content.toString().toLowerCase());
            }
        } catch (IOException ex) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

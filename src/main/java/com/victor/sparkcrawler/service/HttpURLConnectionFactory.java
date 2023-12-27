package com.victor.sparkcrawler.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public interface HttpURLConnectionFactory {
    HttpURLConnection createHttpURLConnection(URL url) throws IOException;
}
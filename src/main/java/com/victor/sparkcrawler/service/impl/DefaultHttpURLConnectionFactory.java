package com.victor.sparkcrawler.service.impl;

import com.victor.sparkcrawler.service.HttpURLConnectionFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DefaultHttpURLConnectionFactory implements HttpURLConnectionFactory {

    @Override
    public HttpURLConnection createHttpURLConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
}

package com.victor.sparkcrawler.service.impl;

import com.victor.sparkcrawler.domain.CrawlerHttpResponse;
import com.victor.sparkcrawler.service.HttpURLConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrawlerHttpServiceTest {

    private CrawlerHttpService crawlerHttpService;
    private HttpURLConnectionFactory mockConnectionFactory;

    @BeforeEach
    void setUp() {
        mockConnectionFactory = mock(HttpURLConnectionFactory.class);
        crawlerHttpService = new CrawlerHttpService(mockConnectionFactory);
    }

    @Test
    void testGetContent_SuccessfulRequest() throws IOException {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        BufferedReader mockReader = mock(BufferedReader.class);

        String url = "https://example.com";

        when(mockConnectionFactory.createHttpURLConnection(any(URL.class))).thenReturn(mockConnection);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream("Mocked Content".getBytes()));
        when(mockReader.readLine()).thenReturn("Mocked Content").thenReturn(null);
        when(mockConnection.getResponseCode()).thenReturn(200);

        CrawlerHttpResponse response = crawlerHttpService.getContent(url);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("mocked content", response.getContent().toLowerCase());
    }

    @Test
    void testGetContent_ConnectionError() throws IOException {
        String url = "https://nonexistenturl123456789.com";
        when(mockConnectionFactory.createHttpURLConnection(any(URL.class))).thenThrow(IOException.class);

        CrawlerHttpResponse response = crawlerHttpService.getContent(url);

        assertNull(response);
    }

    @Test
    void testGetContent_InvalidURL() {
        String url = "not a valid URL";

        CrawlerHttpResponse response = crawlerHttpService.getContent(url);

        assertNull(response);
    }
}
package com.victor.sparkcrawler.service.impl;

import com.google.gson.Gson;
import com.victor.sparkcrawler.domain.SearchResult;
import com.victor.sparkcrawler.service.HttpService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CrawlerServiceTest {

    private CrawlerService crawlerService;
    private ExecutorService executorService;
    private HttpService httpService;
    private Gson gson;
    private String baseUrl = "https://example.com/";

    @BeforeEach
    void setUp() {
        executorService = Executors.newSingleThreadExecutor();
        httpService = mock(HttpService.class);
        gson = new Gson();
        crawlerService = new CrawlerService(executorService, httpService, gson);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdown();
    }

    @Test
    void getSearchResults_validRequest_returnsJson() {
        Request request = mock(Request.class);
        when(request.params(":id")).thenReturn("validId");
        SearchResult searchResult = new SearchResult("validId", "active", new ConcurrentLinkedQueue<>());
        crawlerService.setSearchResultsMap("validId", searchResult);

        String result = crawlerService.getSearchResults(request, gson);

        assertNotNull(result);
        assertTrue(result.contains("validId"));
    }

    @Test
    void getSearchResults_invalidRequest_throwsException() {
        Request request = mock(Request.class);
        when(request.params(":id")).thenReturn("invalidId");

        assertThrows(NoSuchElementException.class, () -> crawlerService.getSearchResults(request, gson));
    }

    @Test
    void searchTerm_validRequest_returnsJsonWithId() {
        Request request = mock(Request.class);
        when(request.body()).thenReturn("{\"keyword\":\"test\"}");

        String result = crawlerService.searchTerm(request, gson);

        assertNotNull(result);
        assertTrue(result.contains("id"));
    }
}

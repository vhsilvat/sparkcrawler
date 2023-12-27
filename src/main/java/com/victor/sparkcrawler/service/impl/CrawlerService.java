package com.victor.sparkcrawler.service.impl;

import com.victor.sparkcrawler.domain.CrawlerHttpResponse;
import com.victor.sparkcrawler.domain.SearchResult;
import com.victor.sparkcrawler.service.HttpService;
import com.victor.sparkcrawler.service.SearchService;
import com.victor.sparkcrawler.util.ConcurrentLinkedSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import spark.Request;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.victor.sparkcrawler.global.Constants.*;


public class CrawlerService implements SearchService {

    private static final ConcurrentHashMap<String, SearchResult> searchResultsMap = new ConcurrentHashMap<>();

    private static final String BASE_URL = System.getenv("BASE_URL");
    private static final String URL_REGEX = "<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1";
    private static final int MAX_RESULT_URLS = 100;
    private static final int MAX_VISITED_URLS = 1000;
    private static final Pattern URL_REGEX_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);

    private final ExecutorService executorService;
    private final HttpService httpService;
    private final Gson gson;

    public CrawlerService(ExecutorService executorService, HttpService httpClient, Gson gson) {

        this.executorService = executorService;
        this.httpService = httpClient;
        this.gson = gson;
    }

    @Override
    public String getSearchResults(Request request, Gson gson) {
        Optional<SearchResult> resultOptional = Optional.ofNullable(searchResultsMap.get(request.params(":id")));

        return resultOptional
                .map(gson::toJson)
                .orElseThrow(() -> new NoSuchElementException(request.params(":id")));
    }

    @Override
    public String searchTerm(Request request, Gson gson) {

        JsonObject requestBody = gson.fromJson(request.body(), JsonObject.class);
        String keyword = requestBody.get("keyword").getAsString();
        String id = generateId();

        SearchResult searchResult = new SearchResult(id, STATUS_ACTIVE, new ConcurrentLinkedQueue<>());
        searchResultsMap.put(id, searchResult);

        CompletableFuture.runAsync(() -> {
            try {
                crawl(keyword, searchResult, httpService);
            } catch (Exception e) {
                throw new IllegalThreadStateException(e.getMessage());
            }
        }, executorService);

        return gson.toJson(Map.of("id", id));
    }

    private void crawl(String keyword, SearchResult searchResult, HttpService httpService) {

        Set<String> visitedUrls = new ConcurrentLinkedSet<>();
        Set<String> urlsToVisit = new ConcurrentLinkedSet<>();
        AtomicInteger keywordOccurrencesCounter = new AtomicInteger(0);

        urlsToVisit.add(BASE_URL);

        do {
            urlsToVisit.parallelStream()
                    .takeWhile(url -> visitedUrls.size() <= MAX_VISITED_URLS)
                    .forEach(currentUrl -> {

                        if (isMaxUrlResultReached(keywordOccurrencesCounter)) return;
                        if (!visitedUrls.contains(currentUrl)) {
                            visitedUrls.add(currentUrl);

                            try {
                                processUrl(currentUrl, keyword, searchResult, keywordOccurrencesCounter,
                                        urlsToVisit, httpService, visitedUrls);

                            } catch (IOException e) {
                                throw new UncheckedIOException(currentUrl, e);
                            }
                        }
                    });
            if (isWebCrawlingFinished(visitedUrls,  urlsToVisit)) break;
        } while (!isMaxUrlResultReached(keywordOccurrencesCounter) && visitedUrls.size() <= MAX_VISITED_URLS);

        executorService.shutdown();
        searchResult.setStatus(STATUS_DONE);
    }

    private void processUrl(String currentUrl, String keyword, SearchResult searchResult,
                            AtomicInteger keywordOccurrencesCounter, Set<String> urlsToVisit,
                            HttpService httpService, Set<String> visitedUrls) throws IOException {

        Optional<CrawlerHttpResponse> httpResponseOptional =
                Optional.ofNullable(httpService.getContent(currentUrl));

        httpResponseOptional
                .filter(response -> response.getStatusCode() == HTTP_STATUS_OK)
                .ifPresent(response -> {

                    checkKeywordOccurrence(response, keyword, searchResult, currentUrl, keywordOccurrencesCounter);
                    extractLinks(response, urlsToVisit, visitedUrls);
                });

    }

    private void checkKeywordOccurrence(CrawlerHttpResponse httpResponse, String keyword, SearchResult searchResult,
                                        String currentUrl, AtomicInteger keywordOccurrencesCounter) {

        if (httpResponse.getContent().contains(keyword)) {

            searchResult.getUrls().add(currentUrl);
            keywordOccurrencesCounter.incrementAndGet();
        }
    }

    private void extractLinks(CrawlerHttpResponse httpResponse, Set<String> urlsToVisit, Set<String> visitedUrls) {

        Matcher matcher = URL_REGEX_PATTERN.matcher(httpResponse.getContent());

        matcher.results()
                .map(match -> match.group(2))
                .filter(this::isValidUrl)
                .map(this::createAbsoluteUrl)
                .filter(Objects::nonNull)
                .filter(url -> url.startsWith(BASE_URL))
                .filter(url -> !visitedUrls.contains(url))
                .forEach(urlsToVisit::add);
    }

    private String createAbsoluteUrl(String url) {
        try {
            URL base = new URL(BASE_URL);
            URI absoluteURI = base.toURI().resolve(url).normalize().toURL().toURI();
            return absoluteURI.toString();
        } catch (MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            return null;
        }
    }

    private boolean isValidUrl(String url) {
        return !url.isEmpty() && !url.startsWith("http");
    }

    private boolean isMaxUrlResultReached(AtomicInteger keywordOccurrencesCounter) {
        return keywordOccurrencesCounter.get() >= MAX_RESULT_URLS;
    }

    private boolean isWebCrawlingFinished(Set<String> visitedUrls, Set<String> urlsToVisit) {
        return visitedUrls.size() == 1 && urlsToVisit.size() == 1;
    }

    private String generateId() {
        // Gerar um ID alfanumérico de 8 caracteres
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // Package-private (default) access method for testing
    void setSearchResultsMap(String key, SearchResult value) {
        searchResultsMap.put(key, value);
    }

}

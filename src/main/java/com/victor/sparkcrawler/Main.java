package com.victor.sparkcrawler;

import com.victor.sparkcrawler.adapter.CrawlerController;
import com.victor.sparkcrawler.global.GlobalExceptionHandler;
import com.victor.sparkcrawler.global.RequestValidator;
import com.victor.sparkcrawler.service.HttpService;
import com.victor.sparkcrawler.service.SearchService;
import com.victor.sparkcrawler.service.impl.CrawlerHttpService;
import com.victor.sparkcrawler.service.impl.CrawlerService;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        HttpService httpService = new CrawlerHttpService();
        Gson gson = new Gson();

        SearchService searchService = new CrawlerService(executorService, httpService, gson);

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        RequestValidator validator = new RequestValidator();

        new CrawlerController(searchService, gson, exceptionHandler, validator).init();
    }
}



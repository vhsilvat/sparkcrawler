package com.victor.sparkcrawler.adapter;

import com.victor.sparkcrawler.global.GlobalExceptionHandler;
import com.victor.sparkcrawler.global.RequestValidator;
import com.victor.sparkcrawler.service.SearchService;
import com.google.gson.Gson;

import java.util.NoSuchElementException;

import static com.victor.sparkcrawler.global.Constants.CONTENT_TYPE_JSON;
import static spark.Spark.*;

public class CrawlerController {

    private final SearchService crawlService;
    private final Gson gson;
    private final GlobalExceptionHandler exceptionHandler;
    private final RequestValidator validator;

    public CrawlerController(SearchService crawlService, Gson gson,
                             GlobalExceptionHandler exceptionHandler, RequestValidator validator) {
        this.crawlService = crawlService;
        this.gson = gson;
        this.exceptionHandler = exceptionHandler;
        this.validator = validator;
    }

    public void init() {

        before((req, res) ->
                res.type(CONTENT_TYPE_JSON));

        before("/crawl", (req, res) ->
            validator.validateRequest(req, gson));

        get("/crawl/:id", (req, res) ->
                crawlService.getSearchResults(req, gson));

        post("/crawl", (req, res) ->
                crawlService.searchTerm(req, gson));

        exception(Exception.class, (ex, req, res) ->
                exceptionHandler.handleException(ex, res, gson));
    }
}

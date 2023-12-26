package com.victor.sparkcrawler.global;

import com.victor.sparkcrawler.domain.ErrorResponse;
import com.google.gson.Gson;

public class GlobalExceptionHandler {

    public void handleException(Exception ex, spark.Response res, Gson gson) {
        int statusCode = 500;
        String errorMessage = "unexpected error occurred: " + ex.getMessage();

        switch (ex.getClass().getSimpleName()) {
            case "NoSuchElementException":
                statusCode = 404;
                errorMessage = "crawl not found: " + ex.getMessage();
                break;
            case "UncheckedIOException":
                errorMessage = "Error processing URL: " + ex.getMessage();
                break;
            case "IllegalThreadStateException":
                errorMessage = "thread exception occurred: " + ex.getMessage();
                break;
            default:
                break;
        }

        res.status(statusCode);
        res.body(gson.toJson(new ErrorResponse(statusCode, errorMessage)));
    }
}

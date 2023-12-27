package com.victor.sparkcrawler.global;

import com.victor.sparkcrawler.domain.ErrorResponse;
import com.google.gson.*;

import static com.victor.sparkcrawler.global.Constants.*;
import static spark.Spark.halt;

public final class RequestValidator {

    public void validateRequest(spark.Request req, Gson gson) {

        String baseUrl = System.getenv("BASE_URL");

        if (baseUrl == null) {
            halt(404, gson.toJson(
                    new ErrorResponse(400, INVALID_URL)));
            return;
        }

        if (isInvalidBaseUrl(baseUrl)) {
            halt(404, gson.toJson(
                    new ErrorResponse(400, INVALID_URL)));
        }

        if (isInvalidRequest(req)) {
            halt(400, gson.toJson(
                    new ErrorResponse(400, REQUEST_BODY_REQUIRED)));
        }

        try {
            JsonObject jsonBody = gson.fromJson(req.body(), JsonObject.class);

            if (isInvalidKeyword(jsonBody)) {
                halt(400, gson.toJson(
                        new ErrorResponse(400, KEYWORD_REQUIRED)));
            }
        } catch (JsonSyntaxException e) {
            halt(400, gson.toJson(
                    new ErrorResponse(400, REQUEST_BODY_INVALID)));
        }

    }

    private boolean isInvalidBaseUrl(String baseUrl) {
        return !baseUrl.startsWith("http://")
                && !baseUrl.startsWith("https://")
                && !baseUrl.endsWith(".com/");
    }

    private boolean isInvalidRequest(spark.Request req) {
        return req.body() == null || req.body().isEmpty();
    }

    private boolean isInvalidKeyword(JsonObject jsonBody) {
        return isMissingKeyword(jsonBody) || isInvalidKeywordSize(jsonBody.get(KEYWORD).toString());
    }

    private boolean isMissingKeyword(JsonObject jsonBody) {
        return !jsonBody.has(KEYWORD) || isKeywordNull(jsonBody);
    }

    private boolean isKeywordNull(JsonObject jsonBody) {
        return jsonBody.get(KEYWORD) instanceof JsonNull;
    }

    private boolean isInvalidKeywordSize(String keyword) {
        if (keyword == null || keyword.length() < 2) return true;
        String keywordWithoutQuotes = keyword.substring(1, keyword.length() - 1);
        return keywordWithoutQuotes.length() < 4 || keywordWithoutQuotes.length() > 32;
    }
}

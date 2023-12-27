package com.victor.sparkcrawler.global;

public class Constants {

    private Constants() {}

    public static final String KEYWORD_REQUIRED = "field 'keyword' is required (from 4 up to 32 chars)";
    public static final String REQUEST_BODY_REQUIRED = "request body is required";
    public static final String REQUEST_BODY_INVALID = "request body must be valid json object";
    public static final String INVALID_URL = "BASE_URL must begin with 'http://' or 'https://'";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String KEYWORD = "keyword";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_DONE = "done";
    public static final int HTTP_STATUS_OK = 200;
}

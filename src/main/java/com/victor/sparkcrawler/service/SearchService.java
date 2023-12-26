package com.victor.sparkcrawler.service;

import com.google.gson.Gson;
import spark.Request;

public interface SearchService {

    String getSearchResults(Request request, Gson gson);
    String searchTerm(Request request, Gson gson);

}

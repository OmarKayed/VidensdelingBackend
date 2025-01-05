package com.example.demo.service;

import com.example.demo.entity.AzureSearch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class AzureSearchService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${azure.search.service-name}")
    private String searchServiceName;

    @Value("${azure.search.index-name}")
    private String indexName;

    @Value("${azure.search.api-key}")
    private String apiKey;

    @Value("${azure.search.api-version}")
    private String apiVersion;

    public List<AzureSearch> searchArticles(String query) {
        List<AzureSearch> results = new ArrayList<>();

        // Azure Cognitive Search API endpoint
        String searchEndpoint = String.format("https://%s.search.windows.net/indexes/%s/docs/search?api-version=%s",
                searchServiceName, indexName, apiVersion);

        // JSON request body for semantic search
        String requestBody = String.format("""
            {
                "search": "%s",
                "queryType": "semantic",
                "semanticConfiguration": "eksamenSemantic",
                "top": 2,
                "queryLanguage": "en-us"
            }
            """, query);

        // Azure AI headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Send POST request
            String response = restTemplate.exchange(searchEndpoint, HttpMethod.POST, entity, String.class).getBody();

            // Parse response JSON
            JsonNode responseJson = objectMapper.readTree(response);
            JsonNode jsonValues = responseJson.get("value");

            if (jsonValues != null) {
                jsonValues.forEach(document -> {
                    // Extract title, description, and URL from search results from Azure Cognitive Search
                    String title = document.has("metadata_storage_name") ? document.get("metadata_storage_name").asText("") : "Ingen titel";
                    String description = document.has("content") ? document.get("content").asText("") : "Ingen descreption";
                    String url = document.has("url") ? document.get("url").asText("") : "Ingen URL";

                    results.add(new AzureSearch(title, description, url));
                });
            }
        } catch (Exception e) {
            System.err.println("Fejl i at finde artikler: " + e.getMessage());
        }

        return results;
    }
}
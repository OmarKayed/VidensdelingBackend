package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AzureAIService {

    private final RestTemplate restTemplate;
    private final String endpoint;
    private final String apiKey;
    private final String apiVersion;
    private final ObjectMapper objectMapper;

    public AzureAIService(@Value("${azure.ai.endpoint}") String endpoint, @Value("${azure.ai.api-key}") String apiKey, @Value("${azure.ai.api-version}") String apiVersion) {
        this.restTemplate = new RestTemplate();
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.apiVersion = apiVersion;
        this.objectMapper = new ObjectMapper();
    }

    public String getAIResponse(String userInput) {
        String requestUrl = endpoint + "/openai/deployments/gpt-4/chat/completions?api-version=" + apiVersion;

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.set("Content-Type", "application/json");

        String requestBody = chatBody(userInput);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return getMessageBody(response.getBody());

        } catch (Exception e) {
            System.err.println("Fejl i Azure: " + e.getMessage());
            return "En fejl opstod. Kontakt Helpdesk, hvis fejlen fortsætter.";
        }
    }

    public String chatBody(String userInput) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode messages = root.putArray("messages");

            ObjectNode messageObject = messages.addObject();
            messageObject.put("role", "user");
            messageObject.put("content", userInput);

            root.put("temperature", 0.7);
            root.put("top_p", 0.95);
            root.put("max_tokens", 800);

            return objectMapper.writeValueAsString(root);

        } catch (Exception e) {
            throw new RuntimeException("Fejl i at skabe en body for chatten", e);
        }
    }

    public String getMessageBody(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("choices").get(0).path("message").path("content").asText();
    }
}
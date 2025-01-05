package com.example.demo.controller;

import com.example.demo.dto.AzureAIRequestDTO;
import com.example.demo.dto.AzureAIResponseDTO;
import com.example.demo.entity.AzureSearch;
import com.example.demo.service.AzureAIService;
import com.example.demo.service.AzureSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
@RequestMapping("/api/ai")
public class AIController {

    private final AzureAIService azureAIService;
    private final AzureSearchService azureSearchService;

    @Autowired
    public AIController(AzureAIService azureAIService, AzureSearchService azureSearchService) {
        this.azureAIService = azureAIService;
        this.azureSearchService = azureSearchService;
    }

    @PostMapping("/ask")
    public ResponseEntity<AzureAIResponseDTO> askQuestion(@RequestBody AzureAIRequestDTO request) {
        String aiResponse = azureAIService.getAIResponse(request.userInput());
        return ResponseEntity.ok(new AzureAIResponseDTO(aiResponse));
    }

    @PostMapping("/search")
    public ResponseEntity<List<AzureSearch>> searchArticles(@RequestBody AzureAIRequestDTO request) {
        String query = request.userInput();

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<AzureSearch> searchResults = azureSearchService.searchArticles(query);
        return ResponseEntity.ok(searchResults);
    }
}
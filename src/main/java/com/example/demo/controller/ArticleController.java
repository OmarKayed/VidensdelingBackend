package com.example.demo.controller;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.ArticleUploadDTO;
import com.example.demo.entity.User;
import com.example.demo.service.BlobStorageService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class ArticleController {

    private final UserService userService;
    private final BlobStorageService blobStorageService;

    @Autowired
    public ArticleController(UserService userService, BlobStorageService blobStorageService) {
        this.userService = userService;
        this.blobStorageService = blobStorageService;
    }

    // Get all knowledge articles
    @GetMapping("/articles")
    public ResponseEntity<?> getKnowledgeArticles() {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("Du skal logge ind for at kunne se vidensartiklerne");
        }

        List<ArticleDTO> articles = blobStorageService.getArticles();
        return ResponseEntity.ok(articles);
    }

    // Get an article by title
    @GetMapping("/articles/{title}")
    public ResponseEntity<?> getArticleByTitle(@PathVariable String title) {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("Du skal logge ind for at se denne artikel");
        }

        ArticleDTO article = blobStorageService.getArticleByTitle(title);
        return ResponseEntity.ok(article);
    }

    // Upload a new article
    @PostMapping("/upload")
    public ResponseEntity<?> uploadArticle(@RequestBody ArticleUploadDTO articleUploadDTO) {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("Du skal logge ind for at uploade en artikel");
        }

        blobStorageService.uploadArticle(articleUploadDTO);
        return ResponseEntity.ok("Din artikel blev uploadet");
    }

    // Delete an article
    @DeleteMapping("/articles/{title}")
    public ResponseEntity<String> deleteArticle(@PathVariable String title) {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("Du skal logge ind for at slette en artikel");
        }

        blobStorageService.deleteArticle(title);
        return ResponseEntity.ok("Din artikel blev slettet");
    }

    // Update an article
    @PutMapping("/articles/{title}")
    public ResponseEntity<?> updateArticle(@PathVariable String title, @RequestBody ArticleUploadDTO updatedArticle) {
        User loggedInUser = userService.getLoggedInUser();

        if (loggedInUser == null) {
            return ResponseEntity.status(401).body("Du skal logge ind for at opdatere en artikel");
        }

        blobStorageService.editArticle(title, updatedArticle);
        return ResponseEntity.ok("Artiklen blev opdateret");
    }
}

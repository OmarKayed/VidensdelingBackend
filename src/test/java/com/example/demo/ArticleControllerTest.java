package com.example.demo;

import com.example.demo.controller.ArticleController;
import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.ArticleUploadDTO;
import com.example.demo.entity.User;
import com.example.demo.enums.UserType;
import com.example.demo.service.BlobStorageService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArticleControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BlobStorageService blobStorageService;

    private ArticleController articleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        articleController = new ArticleController(userService, blobStorageService);
    }

    @Test
    void testGetKnowledgeArticlesLoggedIn() {
        // Mocking logged in user
        User loggedInUser = new User("user", "password", "user@google.com", null, UserType.USER);
        when(userService.getLoggedInUser()).thenReturn(loggedInUser);

        // Mocking articles
        List<ArticleDTO> articles = List.of(new ArticleDTO("Test Title", "Test Description", "http://example.com"));
        when(blobStorageService.getArticles()).thenReturn(articles);
    }

    @Test
    void testGetKnowledgeArticlesLoggedOut() {
        when(userService.getLoggedInUser()).thenReturn(null);

        ResponseEntity<?> response = articleController.getKnowledgeArticles();
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Du skal logge ind for at kunne se vidensartiklerne", response.getBody());
    }

    @Test
    void testGetArticleByTitleLoggedIn() {
        User loggedInUser = new User("user", "password", "user@google.com", null, UserType.USER);
        when(userService.getLoggedInUser()).thenReturn(loggedInUser);

        ArticleDTO article = new ArticleDTO("Test Title", "Test Description", "http://google.com");
        when(blobStorageService.getArticleByTitle("Test Title")).thenReturn(article);

        ResponseEntity<?> response = articleController.getArticleByTitle("Test Title");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(article, response.getBody());
    }

    @Test
    void testGetArticleByTitleLoggedOut() {
        when(userService.getLoggedInUser()).thenReturn(null);

        ResponseEntity<?> response = articleController.getArticleByTitle("Test Title");
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Du skal logge ind for at se denne artikel", response.getBody());
    }

    @Test
    void testUploadArticleLoggedIn() {
        User loggedInUser = new User("user", "password", "user@google.com", null, UserType.USER);
        when(userService.getLoggedInUser()).thenReturn(loggedInUser);

        ArticleUploadDTO articleUploadDTO = new ArticleUploadDTO("Test Title", "Test Description");

        ResponseEntity<?> response = articleController.uploadArticle(articleUploadDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Din artikel blev uploadet", response.getBody());
    }

    @Test
    void testUploadArticleLoggedOut() {
        when(userService.getLoggedInUser()).thenReturn(null);

        ArticleUploadDTO articleUploadDTO = new ArticleUploadDTO("Test Title", "Test Description");

        ResponseEntity<?> response = articleController.uploadArticle(articleUploadDTO);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Du skal logge ind for at uploade en artikel", response.getBody());
    }

    @Test
    void testDeleteArticleLoggedIn() {
        User loggedInUser = new User("user", "password", "user@google.com", null, UserType.USER);
        when(userService.getLoggedInUser()).thenReturn(loggedInUser);

        ResponseEntity<String> response = articleController.deleteArticle("Test Title");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Din artikel blev slettet", response.getBody());
    }

    @Test
    void testDeleteArticleLoggedOut() {
        when(userService.getLoggedInUser()).thenReturn(null);

        ResponseEntity<String> response = articleController.deleteArticle("Test Title");
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Du skal logge ind for at slette en artikel", response.getBody());
    }

    @Test
    void testUpdateArticleLoggedIn() {
        User loggedInUser = new User("user", "password", "user@google.com", null, UserType.USER);
        when(userService.getLoggedInUser()).thenReturn(loggedInUser);

        ArticleUploadDTO updatedArticle = new ArticleUploadDTO("Updated Title", "Updated Description");

        ResponseEntity<?> response = articleController.updateArticle("Original Title", updatedArticle);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Artiklen blev opdateret", response.getBody());
    }

    @Test
    void testUpdateArticleLoggedOut() {
        when(userService.getLoggedInUser()).thenReturn(null);

        ArticleUploadDTO updatedArticle = new ArticleUploadDTO("Updated Title", "Updated Description");

        ResponseEntity<?> response = articleController.updateArticle("Original Title", updatedArticle);
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Du skal logge ind for at opdatere en artikel", response.getBody());
    }
}
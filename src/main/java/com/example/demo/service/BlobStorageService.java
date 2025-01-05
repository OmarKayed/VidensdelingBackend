package com.example.demo.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.ArticleUploadDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlobStorageService {

    private final BlobServiceClient blobServiceClient;
    private final String containerName;
    private final String sasToken;

    public BlobStorageService(BlobServiceClient blobServiceClient, @Value("${azure.storage.container-name}") String containerName, @Value("${azure.storage.sas-token}") String sasToken) {
        this.blobServiceClient = blobServiceClient;
        this.containerName = containerName;
        this.sasToken = sasToken;
    }

    // Fetch all articles
    public List<ArticleDTO> getArticles() {
        List<ArticleDTO> articles = new ArrayList<>();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        for (BlobItem blobItem : containerClient.listBlobs()) {
            String blobName = blobItem.getName();
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            String description = new String(blobClient.downloadContent().toBytes(), StandardCharsets.UTF_8);
            String viewUrl = blobClient.getBlobUrl() + "?" + sasToken;

            articles.add(new ArticleDTO(blobName, description, viewUrl));
        }
        return articles;
    }

    // Upload an article
    public void uploadArticle(ArticleUploadDTO articleUploadDTO) {
        BlobClient blobClient = blobServiceClient
                .getBlobContainerClient(containerName)
                .getBlobClient(articleUploadDTO.title());

        byte[] contentBytes = articleUploadDTO.description().getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream contentStream = new ByteArrayInputStream(contentBytes);

        blobClient.upload(contentStream, contentBytes.length, true); // Ensure length matches
    }

    // Edit an article
    public void editArticle(String originalTitle, ArticleUploadDTO updatedArticle) {
        BlobClient blobClient = blobServiceClient
                .getBlobContainerClient(containerName)
                .getBlobClient(originalTitle);

        if (!blobClient.exists()) {
            throw new RuntimeException("Artiklen blev ikke fundet.");
        }

        // If title changes, delete the old blob and create a new one
        if (!originalTitle.equals(updatedArticle.title())) {
            blobClient.delete();
            blobClient = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(updatedArticle.title());
        }

        byte[] contentBytes = updatedArticle.description().getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream contentStream = new ByteArrayInputStream(contentBytes);

        blobClient.upload(contentStream, contentBytes.length, true); // Ensure length matches
    }

    // Delete an article
    public void deleteArticle(String title) {
        BlobClient blobClient = blobServiceClient
                .getBlobContainerClient(containerName)
                .getBlobClient(title);

        if (blobClient.exists()) {
            blobClient.delete();
        } else {
            throw new RuntimeException("Artiklen blev ikke fundet.");
        }
    }

    // Get an article by title
    public ArticleDTO getArticleByTitle(String title) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(title);

        if (!blobClient.exists()) {
            throw new RuntimeException("Artiklen blev ikke fundet.");
        }

        String description = new String(blobClient.downloadContent().toBytes(), StandardCharsets.UTF_8);
        String viewUrl = blobClient.getBlobUrl() + "?" + sasToken;

        return new ArticleDTO(title, description, viewUrl);
    }
}
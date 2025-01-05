package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AzureSearch {
    private String title;
    private String description;
    private String url;

    public AzureSearch(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }
}
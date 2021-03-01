package com.example.sdktest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Recipe {

    private String id;
    private String name;
    private List<String> ingredients;
    private List<String> servingSuggestions;
    
    public Recipe() {
        this.ingredients = new ArrayList<String>();
    }

    public Recipe(String name, List<String> ingredients, List<String> servingSuggestions) {
        this.setId(this.generateId());
        this.setName(name);
        this.setIngredients(ingredients);
        this.setServingSuggestions(servingSuggestions);
    }

    @DynamoDbPartitionKey
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public List<String> getIngredients() { return this.ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getServingSuggestions() { return this.servingSuggestions; }
    public void setServingSuggestions(List<String> servingSuggestions) { this.servingSuggestions = servingSuggestions; }

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    public String getSuggestionsPrintable() {
        return this.servingSuggestions.stream().collect(Collectors.joining(", or"));
    }
}

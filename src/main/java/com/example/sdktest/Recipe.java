package com.example.sdktest;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Recipe {

    private int id;
    private String name;
    private List<String> ingredients;
    private List<String> servingSuggestions;
    
    public Recipe() {
        this.ingredients = new ArrayList<String>();
    }

    @DynamoDbPartitionKey
    public int getId() { return this.id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public List<String> getIngredients() { return this.ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getServingSuggestions() { return this.servingSuggestions; }
    public void setServingSuggestions(List<String> servingSuggestions) { this.servingSuggestions = servingSuggestions; }
}

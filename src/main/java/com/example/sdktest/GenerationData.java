package com.example.sdktest;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class GenerationData {
    
    private String category;
    private List<String> words;

    public GenerationData() {
        this.words = new ArrayList<String>();
    }

    @DynamoDbPartitionKey
    public String getCategory() { return this.category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getWords() { return this.words; }
    public void setWords(List<String> words) { this.words = words; }
}

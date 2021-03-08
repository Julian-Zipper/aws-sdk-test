package com.example.sdktest;

import java.net.URI;
import java.net.URISyntaxException;

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class RecipeIntegrationTest {

    private static DynamoDbClient ddbc;
    private static DynamoDbEnhancedClient enhancedClient;

    @ClassRule
    public static LocalDbCreationRule dynamoDB = new LocalDbCreationRule();

    @BeforeClass
    public static void setupClass() throws URISyntaxException {

        // ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.builder()
        //     .profileName("default")
        //     .build();

        // System.out.printf("accessKeyId = %s", credentialsProvider.resolveCredentials().accessKeyId());
        // System.out.printf("secretAccessKey = %s", credentialsProvider.resolveCredentials().secretAccessKey());

        ddbc = DynamoDbClient.builder()
            // .credentialsProvider(credentialsProvider)
            .endpointOverride(new URI("http://localhost:8000"))
            .build();
        
        enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(ddbc)
            .build();
    }

    @Before
    public void setup() {
        CreateTableRequest createTableRequest = new CreateTableRequest()
            .withAttributeDefinitions(new AttributeDefinition("Name", ScalarAttributeType.S))
            .withKeySchema(new KeySchemaElement("Name", KeyType.HASH))
            .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L))
            .withTableName("testRecipes");
        
        DynamoDbTable<Recipe> table = enhancedClient.table("recipes", TableSchema.fromBean(Recipe.class));

        // TODO: create table and write some test for it , this is a WIP
        CreateTableEnhancedRequest request = new CreateTable
        table.createTable(request);
        ddbc.createTable(createTableRequest)
    }

    @Test
    public void dostuff() {

    }

    
}

package org.carlosacademic.config;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoConfig {

    private static final DynamoDbClient dynamoDbClient =
            DynamoDbClient.builder()
                    .region(Region.SA_EAST_1)
                    .build();

    private static final DynamoDbEnhancedClient enhancedClient =
            DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(dynamoDbClient)
                    .build();

    public static DynamoDbEnhancedClient getEnhancedClient() {
        return enhancedClient;
    }
}

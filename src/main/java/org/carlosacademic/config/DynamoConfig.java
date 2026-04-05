package org.carlosacademic.config;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoConfig {

    private static final DynamoDbClient dynamoDbClient =
            DynamoDbClient.builder()
                    .region(Region.SA_EAST_1)
                    .overrideConfiguration(ClientOverrideConfiguration.builder()
                            .addExecutionInterceptor(new TracingInterceptor())
                            .build())
                    .build();

    private static final DynamoDbEnhancedClient enhancedClient =
            DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(dynamoDbClient)
                    .build();

    public static DynamoDbEnhancedClient getEnhancedClient() {
        return enhancedClient;
    }
}

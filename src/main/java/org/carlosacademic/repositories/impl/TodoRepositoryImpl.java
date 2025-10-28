package org.carlosacademic.repositories.impl;

import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.table.DTodo;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class TodoRepositoryImpl implements TodoRepository {

    private final DynamoDbTable<DTodo> table;
    private static final String TODO_TABLE = System.getenv("TODO_TABLE_NAME");

    public TodoRepositoryImpl(){
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.SA_EAST_1)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.table =enhancedClient.table(TODO_TABLE, TableSchema.fromBean(DTodo.class));
    }

    @Override
    public void save(DTodo dTodo) {
        table.putItem(dTodo);
    }
}

package org.carlosacademic.repositories.impl;

import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.table.DTodo;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class TodoRepositoryImpl implements TodoRepository {

    private final DynamoDbTable<DTodo> table;

    public TodoRepositoryImpl(DynamoDbEnhancedClient enhancedClient, String todoTableName){
        this.table =enhancedClient.table(todoTableName, TableSchema.fromBean(DTodo.class));
    }

    @Override
    public void save(DTodo dTodo) {
        table.putItem(dTodo);
    }
}

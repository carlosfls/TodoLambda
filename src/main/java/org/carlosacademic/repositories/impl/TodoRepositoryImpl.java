package org.carlosacademic.repositories.impl;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.table.DTodo;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

public class TodoRepositoryImpl implements TodoRepository {

   private final DynamoDbTable<DTodo> table;

    public TodoRepositoryImpl(DynamoDbEnhancedClient enhancedClient, String todoTableName){
        this.table =enhancedClient.table(todoTableName, TableSchema.fromBean(DTodo.class));
    }

    @Override
    public void save(DTodo dTodo) {
        table.putItem(dTodo);
    }

    @Override
    public void saveIfNotExist(DTodo dTodo, LambdaLogger logger) {
        try {
            PutItemEnhancedRequest<DTodo> request = buildPutItemRequest(dTodo);
            table.putItem(request);
        }catch (ConditionalCheckFailedException e){
            logger.log("Todo already exists ignoring the save operation");
        }
    }

    private PutItemEnhancedRequest<DTodo> buildPutItemRequest(DTodo dTodo) {

        return PutItemEnhancedRequest.builder(DTodo.class)
                .item(dTodo)
                .conditionExpression(
                        Expression.builder()
                                .expression("attribute_not_exists(id)")
                                .build()
                )
                .build();
    }
}

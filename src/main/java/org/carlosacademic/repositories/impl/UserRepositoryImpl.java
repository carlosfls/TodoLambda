package org.carlosacademic.repositories.impl;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.carlosacademic.repositories.UserRepository;
import org.carlosacademic.table.DUser;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

public class UserRepositoryImpl implements UserRepository {

    private final DynamoDbTable<DUser> table;

    public UserRepositoryImpl(DynamoDbEnhancedClient enhancedClient, String userTableName) {
        this.table = enhancedClient.table(userTableName, TableSchema.fromBean(DUser.class));
    }

    @Override
    public void saveIfNotExist(DUser dUser, LambdaLogger logger) {
        try {
            PutItemEnhancedRequest<DUser> request = buildPutItemRequest(dUser);
            table.putItem(request);
        }catch (ConditionalCheckFailedException e){
            logger.log("User already exists ignoring the save operation");
        }
    }

    private PutItemEnhancedRequest<DUser> buildPutItemRequest(DUser dUser) {

        return PutItemEnhancedRequest.builder(DUser.class)
                .item(dUser)
                .conditionExpression(
                        Expression.builder()
                                .expression("attribute_not_exists(id)")
                                .build()
                )
                .build();
    }
}

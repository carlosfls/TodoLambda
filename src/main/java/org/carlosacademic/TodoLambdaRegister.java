package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlosacademic.config.DynamoConfig;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.domain.exceptions.InvalidMessageException;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.repositories.impl.TodoRepositoryImpl;
import org.carlosacademic.service.TodoService;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.ArrayList;
import java.util.List;

public class TodoLambdaRegister implements RequestHandler<SQSEvent, SQSBatchResponse> {

    private static final String TODO_TABLE = System.getenv("TODO_TABLE_NAME");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final TodoService todoService;

    public TodoLambdaRegister() {
        TodoRepository todoRepository = new TodoRepositoryImpl(DynamoConfig.getEnhancedClient(), TODO_TABLE);
        todoService = new TodoService(todoRepository);
    }

    @Override
    public SQSBatchResponse handleRequest(SQSEvent event, Context context) {
        List<SQSBatchResponse.BatchItemFailure> failedMessages = new ArrayList<>();
        String correlationId = "";
        LambdaLogger logger = context.getLogger();

        for (SQSEvent.SQSMessage message : event.getRecords()){
            try {
                correlationId = message.getMessageAttributes()
                        .get("correlationId")
                        .getStringValue();

                TodoDTO todoDTO = getTodoDtoFromMessage(message);

                logger.log(String.format(
                        "{\"message\":\"saving_todo\",\"todoId\":%d,\"correlationId\":\"%s\",\"status\":\"START\"}",
                        todoDTO.id(),
                        correlationId
                ));

                todoService.register(todoDTO, logger);

                logger.log(String.format(
                        "{\"message\":\"saving_todo\",\"todoId\":%d,\"correlationId\":\"%s\",\"status\":\"SUCCESS\"}",
                        todoDTO.id(),
                        correlationId
                ));
            }catch (Exception e){
                handleErrors(e, logger, correlationId);
            }
        }
        return new SQSBatchResponse(failedMessages);
    }

    private TodoDTO getTodoDtoFromMessage(SQSEvent.SQSMessage message) {
        try {
            return objectMapper.readValue(message.getBody(), TodoDTO.class);
        } catch (Exception e) {
            throw new InvalidMessageException(
                    String.format("Invalid message body: %s", message.getBody())
            );
        }
    }

    private void handleErrors(Exception e, LambdaLogger logger, String correlationId) {
        if (e instanceof InvalidMessageException) {
            logger.log(String.format(
                    "{\"message\":\"mapping_todo\",\"error\":%s,\"correlationId\":\"%s\",\"status\":\"FAILED\"}",
                    e.getMessage(),
                    correlationId
            ));
        }else {
            logger.log(String.format(
                    "{\"message\":\"unexpected_error\",\"error\":%s,\"correlationId\":\"%s\",\"status\":\"FAILED\"}",
                    e.getMessage(),
                    correlationId
            ));
        }
    }
}

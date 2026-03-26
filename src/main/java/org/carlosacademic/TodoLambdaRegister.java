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

                logger.log("Converting the message into TodoDto");
                TodoDTO todoDTO = getTodoDtoFromMessage(message);

                logger.log("Saving the TodoDto");
                todoService.register(todoDTO, logger, correlationId);
            }catch (InvalidMessageException e){
                logger.log("Invalid Message: " + e.getMessage() + " RequestId: " + correlationId);
            }catch (Exception e){
                logger.log("Unexpected Error: " + e.getMessage()+ " RequestId: " + correlationId);
                failedMessages.add(new SQSBatchResponse.
                        BatchItemFailure(message.getMessageId())
                );
            }
        }
        return new SQSBatchResponse(failedMessages);
    }

    private TodoDTO getTodoDtoFromMessage(SQSEvent.SQSMessage message) {
        try {
            return objectMapper.readValue(message.getBody(), TodoDTO.class);
        } catch (JsonProcessingException e) {
            throw new InvalidMessageException("Invalid Json: " + message.getBody());
        }
    }
}

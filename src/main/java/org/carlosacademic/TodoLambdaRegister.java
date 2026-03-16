package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TodoLambdaRegister implements RequestHandler<SQSEvent, SQSBatchResponse> {

    private static final Logger logger = LoggerFactory.getLogger(TodoLambdaRegister.class);

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

        logger.info("Request id: {}", context.getAwsRequestId());
        for (SQSEvent.SQSMessage message : event.getRecords()){
            try {
                TodoDTO todoDTO = getTodoDtoFromMessage(message);
                todoService.register(todoDTO, logger);
            }catch (InvalidMessageException e){
                logger.error("Invalid Message: {}", e.getMessage());
            }catch (Exception e){
                logger.error("Error processing the message: {}", e.getMessage());
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

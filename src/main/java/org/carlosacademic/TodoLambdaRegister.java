package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlosacademic.config.DynamoConfig;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.domain.TodoFullDTO;
import org.carlosacademic.domain.UserDTO;
import org.carlosacademic.domain.exceptions.InvalidMessageException;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.repositories.UserRepository;
import org.carlosacademic.repositories.impl.TodoRepositoryImpl;
import org.carlosacademic.repositories.impl.UserRepositoryImpl;
import org.carlosacademic.service.TodoService;
import org.carlosacademic.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class TodoLambdaRegister implements RequestHandler<SQSEvent, SQSBatchResponse> {

    private static final String TODO_TABLE = System.getenv("TODO_TABLE_NAME");
    private static final String USER_TABLE = System.getenv("USER_TABLE_NAME");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final TodoService todoService;
    private final UserService userService;

    public TodoLambdaRegister() {
        TodoRepository todoRepository = new TodoRepositoryImpl(DynamoConfig.getEnhancedClient(), TODO_TABLE);
        UserRepository userRepository = new UserRepositoryImpl(DynamoConfig.getEnhancedClient(), USER_TABLE);
        todoService = new TodoService(todoRepository);
        userService = new UserService(userRepository);
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

                logger.log("Getting sqs message for processing. Request id: " + correlationId);
                TodoFullDTO todoFullDTO = getTodoFullFromMessage(message);

                logger.log("Getting the todo from message. Request id: " + correlationId);
                TodoDTO todoDTO = todoFullDTO.todo();

                logger.log("Saving todo if not exist. Request id: " + correlationId);
                todoService.register(todoDTO, logger);

                logger.log("Getting the user from message. Request id: " + correlationId);
                UserDTO userDTO = todoFullDTO.user();

                logger.log("Saving user if not exist. Request id: " + correlationId);
                userService.register(userDTO, logger);

                logger.log("The user and is todo has been processed. Request id: " + correlationId);
            }catch (Exception e){
                handleErrors(e, logger, correlationId);
            }
        }
        return new SQSBatchResponse(failedMessages);
    }

    private TodoFullDTO getTodoFullFromMessage(SQSEvent.SQSMessage message) {
        try {
            return objectMapper.readValue(message.getBody(), TodoFullDTO.class);
        } catch (Exception e) {
            throw new InvalidMessageException(
                    String.format("Invalid message body: %s", message.getBody())
            );
        }
    }

    private void handleErrors(Exception e, LambdaLogger logger, String correlationId) {
        if (e instanceof InvalidMessageException) {
            logger.log(String.format(
                    "{\"message\":\"invalid_message\",\"error\":%s,\"correlationId\":\"%s\",\"status\":\"FAILED\"}",
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

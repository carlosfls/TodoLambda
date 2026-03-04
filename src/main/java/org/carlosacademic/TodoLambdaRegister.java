package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlosacademic.config.DynamoConfig;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.repositories.impl.TodoRepositoryImpl;
import org.carlosacademic.service.TodoService;

public class TodoLambdaRegister implements RequestHandler<SQSEvent, TodoDTO> {

    private static final String TODO_TABLE = System.getenv("TODO_TABLE_NAME");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final TodoService todoService;

    public TodoLambdaRegister() {
        TodoRepository todoRepository = new TodoRepositoryImpl(DynamoConfig.getEnhancedClient(), TODO_TABLE);
        todoService = new TodoService(todoRepository);
    }

    @Override
    public TodoDTO handleRequest(SQSEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        for (SQSEvent.SQSMessage message : event.getRecords()){
            try {
                logger.log("Receiving the event: " + message.getBody());
                TodoDTO todoDTO = objectMapper.readValue(message.getBody(), TodoDTO.class);
                return todoService.register(todoDTO, logger);
            }catch (Exception e){
                logger.log("Error processing the message: " + message.getBody());
            }
        }
        return null;
    }
}

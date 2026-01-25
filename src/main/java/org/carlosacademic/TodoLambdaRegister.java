package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.service.TodoService;

public class TodoLambdaRegister implements RequestHandler<SQSEvent, TodoDTO> {

    private final TodoService todoService;
    private final ObjectMapper objectMapper;

    public TodoLambdaRegister() {
        todoService = new TodoService();
        objectMapper = new ObjectMapper();
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

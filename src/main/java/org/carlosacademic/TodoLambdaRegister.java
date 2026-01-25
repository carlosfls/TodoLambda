package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.service.TodoService;

public class TodoLambdaRegister implements RequestHandler<TodoDTO, TodoDTO> {

    private final TodoService todoService;

    public TodoLambdaRegister() {
        todoService = new TodoService();
    }

    @Override
    public TodoDTO handleRequest(TodoDTO event, Context context) {
        return todoService.register(event, context);
    }
}

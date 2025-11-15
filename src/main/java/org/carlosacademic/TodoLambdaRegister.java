package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.domain.CreateTodo;
import org.carlosacademic.service.TodoService;

public class TodoLambdaRegister implements RequestHandler<CreateTodo, String> {

    private final TodoService todoService;

    public TodoLambdaRegister() {
        todoService = new TodoService();
    }

    @Override
    public String handleRequest(CreateTodo event, Context context) {
        return todoService.createTodo(event.getId(), context.getLogger());
    }



}

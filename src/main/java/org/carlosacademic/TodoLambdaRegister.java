package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.service.TodoService;

public class TodoLambdaRegister implements RequestHandler<String, String> {

    private final TodoService todoService;

    public TodoLambdaRegister() {
        todoService = new TodoService();
    }

    @Override
    public String handleRequest(String s, Context context) {
        return todoService.createTodo(s);
    }

}

package org.carlosacademic.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.mapper.TodoMapper;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.repositories.impl.TodoRepositoryImpl;
import org.carlosacademic.table.DTodo;

public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService() {
        todoRepository = new TodoRepositoryImpl();

    }

    public TodoDTO register(TodoDTO todo, LambdaLogger logger){
        if (todo != null){
            DTodo dTodo = TodoMapper.toDTodo(todo);
            todoRepository.save(dTodo);
            logger.log("Todo saved successfully");
            return TodoMapper.toTodoDto(dTodo);
        }
        logger.log("Cannot save the todo");
        return null;
    }
}

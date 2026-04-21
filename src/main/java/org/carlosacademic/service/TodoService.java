package org.carlosacademic.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.domain.exceptions.InvalidMessageException;
import org.carlosacademic.mapper.TodoMapper;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.table.DTodo;

public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;

    }

    public void register(TodoDTO todo, LambdaLogger logger) {
        if (todo != null){
            DTodo dTodo = TodoMapper.toDTodo(todo);
            todoRepository.saveIfNotExist(dTodo, logger);
            return;
        }
        throw new InvalidMessageException("TodoDTO is null");
    }
}

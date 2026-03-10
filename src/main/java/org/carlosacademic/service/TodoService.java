package org.carlosacademic.service;

import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.mapper.TodoMapper;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.table.DTodo;
import org.slf4j.Logger;

public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;

    }

    public TodoDTO register(TodoDTO todo, Logger logger){
        if (todo != null){
            DTodo dTodo = TodoMapper.toDTodo(todo);
            todoRepository.save(dTodo);
            logger.info("Todo saved successfully");
            return TodoMapper.toTodoDto(dTodo);
        }
        logger.warn("Cannot save the todo");
        return null;
    }
}

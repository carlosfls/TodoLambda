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

    public void register(TodoDTO todo, Logger logger, String correlationId) {
        if (todo != null){
            logger.info("EVENT=SAVE_TODO todoId={} requestId={}", todo.id(), correlationId);
            DTodo dTodo = TodoMapper.toDTodo(todo);
            todoRepository.save(dTodo);
            logger.info("EVENT=SAVE_TODO STATUS=SUCCESS requestId={}", correlationId);
            return;
        }
        logger.info("EVENT=SAVE_TODO STATUS=ERROR requestId={}", correlationId);
    }
}

package org.carlosacademic.mapper;

import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.table.DTodo;

public class TodoMapper {

    public static DTodo toDTodo(TodoDTO todoDTO){
        DTodo dTodo = new DTodo();
        dTodo.setUserId(todoDTO.userId());
        dTodo.setId(todoDTO.id());
        dTodo.setTitle(todoDTO.title());
        dTodo.setCompleted(todoDTO.completed());
        return dTodo;
    }
}

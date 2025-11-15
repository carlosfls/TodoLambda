package org.carlosacademic.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlosacademic.domain.TodoDTO;
import org.carlosacademic.mapper.TodoMapper;
import org.carlosacademic.repositories.TodoRepository;
import org.carlosacademic.repositories.impl.TodoRepositoryImpl;
import org.carlosacademic.table.DTodo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TodoService {

    private final TodoRepository todoRepository;
    private final ObjectMapper objectMapper;
    private static final String API_URL = System.getenv("TODO_API_URL");
    private LambdaLogger logger;

    public TodoService() {
        objectMapper = new ObjectMapper();
        todoRepository = new TodoRepositoryImpl();
    }

    public TodoDTO createTodo(String id, LambdaLogger logger){
        this.logger = logger;
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL +"/todos/"+id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 && response.body()!=null){
                return register(response.body());
            }else{
                logger.log("Failed creating todo whith status code: "+ response.statusCode());
                return null;
            }
        }catch (Exception e){
            logger.log("Error creating the todo "+ e.getMessage());
        }
        return null;
    }

    public TodoDTO register(String todo){
        if (!todo.isEmpty()){
            try{
                logger.log("Todo string to register: "+ todo);
                TodoDTO t = objectMapper.readValue(todo, TodoDTO.class);
                DTodo dTodo = TodoMapper.toDTodo(t);
                logger.log("Table created: "+ dTodo);
                todoRepository.save(dTodo);
                return TodoMapper.toTodoDto(dTodo);
            } catch (JsonProcessingException e) {
                logger.log("Error parsing json "+ e.getMessage());
            }
        }
        return null;
    }
}
